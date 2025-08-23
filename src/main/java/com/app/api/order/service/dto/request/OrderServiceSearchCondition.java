package com.app.api.order.service.dto.request;

import com.app.domain.order.constant.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderServiceSearchCondition {

    private final String memberName;
    private final OrderStatus orderStatus;

    @Builder
    private OrderServiceSearchCondition(String memberName, OrderStatus orderStatus) {
        this.memberName = memberName;
        this.orderStatus = orderStatus;
    }
}
