package com.shopch.api.order.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderCreateServiceRequest {

    private final Long productId;
    private final Integer orderQuantity;

    @Builder
    private OrderCreateServiceRequest(Long productId, Integer orderQuantity) {
        this.productId = productId;
        this.orderQuantity = orderQuantity;
    }
}
