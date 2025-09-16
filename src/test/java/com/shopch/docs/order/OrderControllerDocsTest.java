package com.shopch.docs.order;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.order.controller.OrderController;
import com.shopch.api.order.controller.dto.OrderCreateRequest;
import com.shopch.api.order.controller.dto.OrderSearchCondition;
import com.shopch.api.order.service.OrderService;
import com.shopch.api.order.service.dto.request.OrderCreateServiceRequest;
import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.api.order.service.dto.response.OrderProductResponse;
import com.shopch.api.order.service.dto.response.OrderResponse;
import com.shopch.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes.ARRAY;
import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes.STRING;
import static com.shopch.domain.order.constant.OrderStatus.INIT;
import static com.shopch.fixture.TimeFixture.*;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerDocsTest extends RestDocsSupport {

    private static final Long PRODUCT_1_ID = 1L;
    private static final Integer MINIMUM_ORDER_QUANTITY = 1;
    private static final String PRODUCT_1_NAME = "product1";
    private static final int PRODUCT_1_ORDER_PRICE = 10000;
    private static final Long ORDER_1_ID = 1L;
    private static final String MEMBER_NAME = "member";
    private static final Long PRODUCT_2_1D = 2L;
    private static final String PRODUCT_2_NAME = "product2";
    private static final int PRODUCT_2_ORDER_PRICE = 20000;
    private static final Long ORDER_2_ID = 2L;

    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @DisplayName("주문 등록")
    @Test
    void createOrder() throws Exception {
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

        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("order-create",
                        requestFields(
                                fieldWithPath("productId").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("orderQuantity").type(NUMBER).description("주문 수량")
                        ),
                        responseFields(
                                fieldWithPath("orderId").type(NUMBER).description("주문 아이디"),
                                fieldWithPath("memberName").type(STRING).description("회원 이름"),
                                fieldWithPath("orderedAt").type(STRING).description("주문 일시"),
                                fieldWithPath("orderStatus").type(STRING).description("주문 상태"),
                                fieldWithPath("totalPrice").type(NUMBER).description("총 주문 금액"),
                                fieldWithPath("orderProducts.[]").type(ARRAY).description("주문 상품 목록"),
                                fieldWithPath("orderProducts.[].productId").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("orderProducts.[].productName").type(STRING).description("상품 이름"),
                                fieldWithPath("orderProducts.[].orderPrice").type(NUMBER).description("주문 금액"),
                                fieldWithPath("orderProducts.[].orderQuantity").type(NUMBER).description("주문 수량")
                        )
                ));
    }

    @DisplayName("주문 취소")
    @Test
    void cancelOrder() throws Exception {
        mockMvc.perform(post("/api/orders/{orderId}/cancel", ORDER_1_ID)
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(document("order-cancel"));
    }

    @DisplayName("검색 주문 및 페이징 정보 조회")
    @Test
    void searchOrders() throws Exception {
        OrderSearchCondition searchCondition = OrderSearchCondition.builder()
                .memberName(MEMBER_NAME)
                .orderStatus(INIT.name())
                .build();

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

        LocalDateTime orderedAt = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);
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

        Pageable pageable = PageRequest.of(0, 2);

        given(orderService.searchOrders(any(OrderServiceSearchCondition.class), any(Pageable.class)))
                .willReturn(PageResponse.of(new PageImpl<>(List.of(orderResponse1, orderResponse2), pageable, 2)));

        mockMvc.perform(get("/api/orders")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .param("memberName", searchCondition.getMemberName())
                        .param("orderStatus", searchCondition.getOrderStatus())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                )
                .andExpect(status().isOk())
                .andDo(document("order-search",
                        responseFields(
                                fieldWithPath("content.[]").type(ARRAY).description("주문 목록"),
                                fieldWithPath("content.[].orderId").type(NUMBER).description("주문 아이디"),
                                fieldWithPath("content.[].memberName").type(STRING).description("회원 이름"),
                                fieldWithPath("content.[].orderedAt").type(STRING).description("주문 일시"),
                                fieldWithPath("content.[].orderStatus").type(STRING).description("주문 상태"),
                                fieldWithPath("content.[].totalPrice").type(NUMBER).description("총 주문 금액"),
                                fieldWithPath("content.[].orderProducts.[]").type(ARRAY).description("주문 상품 목록"),
                                fieldWithPath("content.[].orderProducts.[].productId").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("content.[].orderProducts.[].productName").type(STRING).description("상품 이름"),
                                fieldWithPath("content.[].orderProducts.[].orderPrice").type(NUMBER).description("주문 금액"),
                                fieldWithPath("content.[].orderProducts.[].orderQuantity").type(NUMBER).description("주문 수량"),
                                fieldWithPath("size").type(NUMBER).description("페이지 사이즈"),
                                fieldWithPath("number").type(NUMBER).description("페이지 번호"),
                                fieldWithPath("totalElements").type(NUMBER).description("전체 주문 수"),
                                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수")
                        )
                ));
    }
}
