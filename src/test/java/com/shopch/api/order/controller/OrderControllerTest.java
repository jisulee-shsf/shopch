package com.shopch.api.order.controller;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.order.controller.dto.OrderCreateRequest;
import com.shopch.api.order.controller.dto.OrderSearchCondition;
import com.shopch.api.order.service.dto.request.OrderCreateServiceRequest;
import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.api.order.service.dto.response.OrderProductResponse;
import com.shopch.api.order.service.dto.response.OrderResponse;
import com.shopch.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static com.shopch.domain.order.constant.OrderStatus.INIT;
import static com.shopch.fixture.TimeFixture.*;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static com.shopch.global.config.clock.ClockConfig.DEFAULT_TIME_ZONE;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends ControllerTestSupport {

    private static final Long PRODUCT_1_ID = 1L;
    private static final Integer MINIMUM_ORDER_QUANTITY = 1;
    private static final String PRODUCT_1_NAME = "product1";
    private static final int PRODUCT_1_ORDER_PRICE = 10000;
    private static final Long ORDER_1_ID = 1L;
    private static final String MEMBER_NAME = "member";
    private static final String BAD_REQUEST_CODE = String.valueOf(HttpStatus.BAD_REQUEST.value());
    private static final Long PRODUCT_2_1D = 2L;
    private static final String PRODUCT_2_NAME = "product2";
    private static final int PRODUCT_2_ORDER_PRICE = 20000;
    private static final Long ORDER_2_ID = 2L;

    @DisplayName("주문 등록 요청을 처리한 후, 등록된 주문 정보를 반환한다.")
    @Test
    void createOrder() throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(PRODUCT_1_ID)
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        OrderProductResponse orderProductResponse = OrderProductResponse.builder()
                .productId(request.getProductId())
                .productName(PRODUCT_1_NAME)
                .orderPrice(PRODUCT_1_ORDER_PRICE)
                .orderQuantity(request.getOrderQuantity())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(ORDER_1_ID)
                .memberName(MEMBER_NAME)
                .orderedAt(LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE))
                .orderStatus(INIT.name())
                .totalPrice(orderProductResponse.getOrderPrice() * orderProductResponse.getOrderQuantity())
                .orderProducts(List.of(orderProductResponse))
                .build();

        given(orderService.createOrder(any(), any(OrderCreateServiceRequest.class), any(LocalDateTime.class)))
                .willReturn(orderResponse);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> invalidProductIdProvider() {
        return Stream.of(
                Arguments.of(null, "[productId] 상품 아이디는 필수입니다."),
                Arguments.of(-1L, "[productId] 상품 아이디는 양수여야 합니다."),
                Arguments.of(0L, "[productId] 상품 아이디는 양수여야 합니다.")
        );
    }

    @DisplayName("주문 등록 시 상품 아이디는 필수이며, 양수여야 한다.")
    @ParameterizedTest
    @MethodSource("invalidProductIdProvider")
    void createOrder_InvalidProductId(Long input, String expectedMessage) throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(input)
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    private static Stream<Arguments> invalidOrderQuantityProvider() {
        return Stream.of(
                Arguments.of(null, "[orderQuantity] 주문 수량은 필수입니다."),
                Arguments.of(-1, "[orderQuantity] 주문 수량은 양수여야 합니다."),
                Arguments.of(0, "[orderQuantity] 주문 수량은 양수여야 합니다.")
        );
    }

    @DisplayName("주문 등록 시 주문 수량은 필수이며, 양수여야 한다.")
    @ParameterizedTest
    @MethodSource("invalidOrderQuantityProvider")
    void createOrder_InvalidOrderQuantity(Integer input, String expectedMessage) throws Exception {
        // given
        OrderCreateRequest request = OrderCreateRequest.builder()
                .productId(PRODUCT_1_ID)
                .orderQuantity(input)
                .build();

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @DisplayName("주문을 취소한다.")
    @Test
    void cancelOrder() throws Exception {
        // when & then
        mockMvc.perform(post("/api/orders/{orderId}/cancel", ORDER_1_ID)
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isNoContent());
    }

    @DisplayName("검색 조건에 해당하는 주문과 페이징 정보를 조회해 반환한다.")
    @Test
    void searchOrders() throws Exception {
        // given
        OrderSearchCondition searchCondition = OrderSearchCondition.builder()
                .memberName(MEMBER_NAME)
                .orderStatus(INIT.name())
                .build();

        Pageable pageable = PageRequest.of(0, 2);

        OrderProductResponse orderProductResponse1 = OrderProductResponse.builder()
                .productId(PRODUCT_1_ID)
                .productName(PRODUCT_1_NAME)
                .orderPrice(PRODUCT_1_ORDER_PRICE)
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        OrderProductResponse orderProductResponse2 = OrderProductResponse.builder()
                .productId(PRODUCT_2_1D)
                .productName(PRODUCT_2_NAME)
                .orderPrice(PRODUCT_2_ORDER_PRICE)
                .orderQuantity(MINIMUM_ORDER_QUANTITY)
                .build();

        LocalDateTime orderedAt = LocalDateTime.ofInstant(INSTANT_NOW, DEFAULT_TIME_ZONE);
        OrderResponse orderResponse1 = OrderResponse.builder()
                .orderId(ORDER_1_ID)
                .memberName(MEMBER_NAME)
                .orderedAt(orderedAt)
                .orderStatus(INIT.name())
                .totalPrice(orderProductResponse1.getOrderPrice() * orderProductResponse1.getOrderQuantity())
                .orderProducts(List.of(orderProductResponse1))
                .build();

        OrderResponse orderResponse2 = OrderResponse.builder()
                .orderId(ORDER_2_ID)
                .memberName(MEMBER_NAME)
                .orderedAt(orderedAt.plus(ONE_SECOND_IN_MILLIS, MILLIS))
                .orderStatus(INIT.name())
                .totalPrice(orderProductResponse2.getOrderPrice() * orderProductResponse2.getOrderQuantity())
                .orderProducts(List.of(orderProductResponse2))
                .build();

        given(orderService.searchOrders(any(OrderServiceSearchCondition.class), any(Pageable.class)))
                .willReturn(PageResponse.of(new PageImpl<>(List.of(orderResponse1, orderResponse2), pageable, 2)));

        // when & then
        mockMvc.perform(get("/api/orders")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .param("memberName", searchCondition.getMemberName())
                        .param("orderStatus", searchCondition.getOrderStatus())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                )
                .andExpect(status().isOk());
    }
}
