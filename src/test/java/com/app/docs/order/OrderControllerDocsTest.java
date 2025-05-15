package com.app.docs.order;

import com.app.api.common.PageResponse;
import com.app.api.order.controller.OrderController;
import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.request.OrderSearchCondition;
import com.app.api.order.dto.response.OrderProductResponse;
import com.app.api.order.dto.response.OrderResponse;
import com.app.api.order.service.OrderService;
import com.app.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.app.domain.order.constant.OrderStatus.INIT;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes.ARRAY;
import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes.STRING;
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

    private final OrderService orderService = mock(OrderService.class);

    @Override
    protected Object initController() {
        return new OrderController(orderService);
    }

    @DisplayName("주문 등록")
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

        given(orderService.createOrder(any(), any(LocalDateTime.class), any(OrderCreateRequest.class)))
                .willReturn(orderResponse);

        // when & then
        mockMvc.perform(post("/api/orders")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
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
                                fieldWithPath("orderDateTime").type(STRING).description("주문 일시"),
                                fieldWithPath("orderStatus").type(STRING).description("주문 상태"),
                                fieldWithPath("totalOrderPrice").type(NUMBER).description("총 주문 금액"),
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
        // given
        Long orderId = 1L;

        // when & then
        mockMvc.perform(post("/api/orders/{orderId}/cancel", orderId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk())
                .andDo(document("order-cancel"));
    }

    @DisplayName("검색 주문 조회")
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
                .andExpect(status().isOk())
                .andDo(document("order-find",
                        responseFields(
                                fieldWithPath("content.[]").type(ARRAY).description("주문 목록"),
                                fieldWithPath("content.[].orderId").type(NUMBER).description("주문 아이디"),
                                fieldWithPath("content.[].memberName").type(STRING).description("회원 이름"),
                                fieldWithPath("content.[].orderDateTime").type(STRING).description("주문 일시"),
                                fieldWithPath("content.[].orderStatus").type(STRING).description("주문 상태"),
                                fieldWithPath("content.[].totalOrderPrice").type(NUMBER).description("총 주문 금액"),
                                fieldWithPath("content.[].orderProducts.[]").type(ARRAY).description("주문 상품 목록"),
                                fieldWithPath("content.[].orderProducts.[].productId").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("content.[].orderProducts.[].productName").type(STRING).description("상품 이름"),
                                fieldWithPath("content.[].orderProducts.[].orderPrice").type(NUMBER).description("주문 금액"),
                                fieldWithPath("content.[].orderProducts.[].orderQuantity").type(NUMBER).description("주문 수량"),
                                fieldWithPath("size").type(NUMBER).description("한 페이지에 포함된 주문 수"),
                                fieldWithPath("number").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("totalElements").type(NUMBER).description("전체 주문 수"),
                                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수")
                        )
                ));
    }
}
