package com.app.api.order.service.dto.response;

import com.app.domain.order.entity.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class OrderResponse {

    private Long orderId;
    private String memberName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime orderedAt;
    private String orderStatus;
    private int totalOrderPrice;
    private List<OrderProductResponse> orderProducts;

    @Builder
    private OrderResponse(Long orderId, String memberName, LocalDateTime orderedAt, String orderStatus, int totalOrderPrice,
                          List<OrderProductResponse> orderProducts) {
        this.orderId = orderId;
        this.memberName = memberName;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
        this.totalOrderPrice = totalOrderPrice;
        this.orderProducts = orderProducts;
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .memberName(order.getMember().getName())
                .orderedAt(order.getOrderedAt())
                .orderStatus(order.getOrderStatus().name())
                .totalOrderPrice(order.getTotalOrderPrice())
                .orderProducts(order.getOrderProducts().stream()
                        .map(OrderProductResponse::of)
                        .collect(Collectors.toList()))
                .build();
    }
}
