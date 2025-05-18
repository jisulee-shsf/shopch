package com.app.api.order.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderServiceSearchCondition {

    private String memberName;
    private String orderStatus;

    @Builder
    private OrderServiceSearchCondition(String memberName, String orderStatus) {
        this.memberName = memberName;
        this.orderStatus = orderStatus;
    }
}
