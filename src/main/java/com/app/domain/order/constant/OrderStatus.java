package com.app.domain.order.constant;

public enum OrderStatus {

    INIT,
    CANCELED;

    public static OrderStatus from(String orderStatus) {
        return OrderStatus.valueOf(orderStatus.toUpperCase());
    }
}
