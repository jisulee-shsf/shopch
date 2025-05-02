package com.app.api.order.dto.response;

import com.app.domain.orderProduct.entity.OrderProduct;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderProductResponse {

    private Long productId;
    private String productName;
    private int orderPrice;
    private int orderQuantity;

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
