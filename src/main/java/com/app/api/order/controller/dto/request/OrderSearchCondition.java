package com.app.api.order.controller.dto.request;

import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.domain.order.constant.OrderStatus;
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

    public OrderServiceSearchCondition toServiceSearchCondition() {
        return OrderServiceSearchCondition.builder()
                .memberName(memberName)
                .orderStatus(OrderStatus.from(orderStatus))
                .build();
    }
}
