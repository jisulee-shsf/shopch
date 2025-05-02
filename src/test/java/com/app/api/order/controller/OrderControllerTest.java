package com.app.api.order.controller;

import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.response.OrderProductResponse;
import com.app.api.order.dto.response.OrderResponse;
import com.app.api.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.List;

import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = OrderController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {
                        WebMvcConfigurer.class,
                        HandlerInterceptor.class,
                        HandlerMethodArgumentResolver.class
                }
        )
)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @DisplayName("주문을 등록한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(1L)
                .orderQuantity(1)
                .build();

        OrderProductResponse orderProductResponse = OrderProductResponse.builder()
                .productId(request.getProductId())
                .productName("product")
                .orderPrice(10000)
                .orderQuantity(request.getOrderQuantity())
                .build();

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(1L)
                .memberName("member")
                .orderDateTime(orderDateTime)
                .orderStatus(INIT.name())
                .totalPrice(orderProductResponse.getOrderPrice() * orderProductResponse.getOrderQuantity())
                .orderProducts(List.of(orderProductResponse))
                .build();

        given(orderService.createOrder(anyLong(), any(LocalDateTime.class), any(OrderCreateRequest.class)))
                .willReturn(orderResponse);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("주문 등록 시 상품 아이디는 필수이다.")
    @Test
    void createOrder_MissingProductId() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(null)
                .orderQuantity(1)
                .build();

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[productId] 상품 아이디는 필수입니다."));
    }

    @DisplayName("주문 등록 시 주문 수량은 양수여야 한다.")
    @Test
    void createOrder_ZeroOrderQuantity() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(1L)
                .orderQuantity(0)
                .build();

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[orderQuantity] 주문 수량은 양수여야 합니다."));
    }
}
