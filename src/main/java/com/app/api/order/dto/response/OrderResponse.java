package com.app.api.order.dto.response;

import com.app.domain.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Getter
public class OrderResponse {

    private Long orderId;
    private String memberName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime orderDateTime;
    private String orderStatus;
    private int totalPrice;
    private List<OrderProductResponse> orderProducts;

    @Builder
    private OrderResponse(Long orderId, String memberName, LocalDateTime orderDateTime, String orderStatus, int totalPrice,
                          List<OrderProductResponse> orderProducts) {
        this.orderId = orderId;
        this.memberName = memberName;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderProducts = orderProducts;
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .memberName(order.getMember().getName())
                .orderDateTime(order.getOrderDateTime())
                .orderStatus(order.getOrderStatus().name())
                .totalPrice(order.getTotalPrice())
                .orderProducts(order.getOrderProducts().stream()
                        .map(OrderProductResponse::of)
                        .collect(toList()))
                .build();
    }
}
