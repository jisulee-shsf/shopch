package com.shopch.api.order.service.dto.response;

import com.shopch.domain.orderProduct.entity.OrderProduct;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductResponse {

    private final Long productId;
    private final String productName;
    private final int orderPrice;
    private final int orderQuantity;

    @Builder
    private OrderProductResponse(Long productId, String productName, int orderPrice, int orderQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.orderPrice = orderPrice;
        this.orderQuantity = orderQuantity;
    }

    public static OrderProductResponse of(OrderProduct orderProduct) {
        return OrderProductResponse.builder()
                .productId(orderProduct.getProduct().getId())
                .productName(orderProduct.getProduct().getName())
                .orderPrice(orderProduct.getOrderPrice())
                .orderQuantity(orderProduct.getOrderQuantity())
                .build();
    }
}
