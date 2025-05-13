package com.app.api.order.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderSearchCondition {

    private String memberName;
    private String orderStatus;

    @Builder
    private OrderSearchCondition(String memberName, String orderStatus) {
        this.memberName = memberName;
        this.orderStatus = orderStatus;
    }
}
