package com.app.api.order.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCreateServiceRequest {

    private Long productId;
    private Integer orderQuantity;

    @Builder
    private OrderCreateServiceRequest(Long productId, Integer orderQuantity) {
        this.productId = productId;
        this.orderQuantity = orderQuantity;
    }
}
