package com.app.api.order.controller.dto.request;

import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.domain.order.constant.OrderStatus;
import com.app.global.validator.EnumValue;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class OrderSearchCondition {

    private String memberName;
    @EnumValue(enumClass = OrderStatus.class, message = "유효하지 않은 주문 상태입니다.")
    private String orderStatus;

    @Builder
    private OrderSearchCondition(String memberName, String orderStatus) {
        this.memberName = memberName;
        this.orderStatus = orderStatus;
    }

    public OrderServiceSearchCondition toServiceSearchCondition() {
        return OrderServiceSearchCondition.builder()
                .memberName(memberName)
                .orderStatus(StringUtils.hasText(orderStatus) ? OrderStatus.from(orderStatus) : null)
                .build();
    }
}
