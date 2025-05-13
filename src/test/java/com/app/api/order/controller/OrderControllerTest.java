package com.app.api.order.controller;

import com.app.api.common.PageResponse;
import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.request.OrderSearchCondition;
import com.app.api.order.dto.response.OrderProductResponse;
import com.app.api.order.dto.response.OrderResponse;
import com.app.api.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .totalOrderPrice(orderProductResponse.getOrderPrice() * orderProductResponse.getOrderQuantity())
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

    @DisplayName("주문 등록 시 상품 아이디는 양수여야 한다.")
    @Test
    void createOrder_ZeroProductId() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(0L)
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
                .andExpect(jsonPath("$.errorMessage").value("[productId] 상품 아이디는 양수여야 합니다."));
    }

    @DisplayName("주문 등록 시 주문 수량은 필수이다.")
    @Test
    void createOrder_MissingOrderQuantity() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(1L)
                .orderQuantity(null)
                .build();

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[orderQuantity] 주문 수량은 필수입니다."));
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

    @DisplayName("주문을 취소한다.")
    @Test
    void cancelOrder() throws Exception {
        // given
        Long orderId = 1L;

        // when & then
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk());
    }

    @DisplayName("검색 조건에 해당하는 주문과 페이징 결과를 조회한다.")
    @Test
    void findOrders() throws Exception {
        // given
        OrderSearchCondition searchCondition = OrderSearchCondition.builder()
                .memberName("member")
                .orderStatus(INIT.name())
                .build();

        PageRequest pageRequest = PageRequest.of(0, 2);

        OrderProductResponse orderProductResponse1 = OrderProductResponse.builder()
                .productId(1L)
                .productName("product")
                .orderPrice(10000)
                .orderQuantity(1)
                .build();

        OrderProductResponse orderProductResponse2 = OrderProductResponse.builder()
                .productId(1L)
                .productName("product")
                .orderPrice(10000)
                .orderQuantity(2)
                .build();

        LocalDateTime orderDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        OrderResponse orderResponse1 = OrderResponse.builder()
                .orderId(1L)
                .memberName("member")
                .orderDateTime(orderDateTime)
                .orderStatus(INIT.name())
                .totalOrderPrice(orderProductResponse1.getOrderPrice() * orderProductResponse1.getOrderQuantity())
                .orderProducts(List.of(orderProductResponse1))
                .build();

        OrderResponse orderResponse2 = OrderResponse.builder()
                .orderId(2L)
                .memberName("member")
                .orderDateTime(orderDateTime.plus(1000, MILLIS))
                .orderStatus(INIT.name())
                .totalOrderPrice(orderProductResponse2.getOrderPrice() * orderProductResponse2.getOrderQuantity())
                .orderProducts(List.of(orderProductResponse2))
                .build();

        Page<OrderResponse> pageResponse = new PageImpl<>(List.of(orderResponse1, orderResponse2), pageRequest, 2);
        given(orderService.findOrders(any(OrderSearchCondition.class), any(Pageable.class)))
                .willReturn(PageResponse.of(pageResponse));

        // when & then
        mockMvc.perform(get("/api/orders")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .param("memberName", searchCondition.getMemberName())
                        .param("orderStatus", searchCondition.getOrderStatus())
                        .param("page", String.valueOf(pageRequest.getPageNumber()))
                        .param("size", String.valueOf(pageRequest.getPageSize()))
                )
                .andExpect(status().isOk());
    }
}
