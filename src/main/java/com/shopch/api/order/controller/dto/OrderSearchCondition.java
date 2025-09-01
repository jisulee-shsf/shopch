package com.shopch.api.order.controller.dto;

import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.domain.order.constant.OrderStatus;
import com.shopch.global.validator.ValueOfEnum;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class OrderSearchCondition {

    private String memberName;

    @ValueOfEnum(enumClass = OrderStatus.class, message = "유효하지 않은 주문 상태입니다.")
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
