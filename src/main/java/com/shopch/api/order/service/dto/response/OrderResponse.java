package com.shopch.api.order.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shopch.domain.order.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {

    private final Long orderId;
    private final String memberName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime orderedAt;

    private final String orderStatus;
    private final int totalPrice;
    private final List<OrderProductResponse> orderProducts;

    @Builder
    private OrderResponse(Long orderId, String memberName, LocalDateTime orderedAt, String orderStatus, int totalPrice,
                          List<OrderProductResponse> orderProducts) {
        this.orderId = orderId;
        this.memberName = memberName;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.orderProducts = orderProducts;
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .memberName(order.getMember().getName())
                .orderedAt(order.getOrderedAt())
                .orderStatus(order.getOrderStatus().name())
                .totalPrice(order.getTotalPrice())
                .orderProducts(order.getOrderProducts().stream()
                        .map(OrderProductResponse::of)
                        .collect(Collectors.toList()))
                .build();
    }
}
