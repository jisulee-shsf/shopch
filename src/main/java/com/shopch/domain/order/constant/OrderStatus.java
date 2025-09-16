package com.shopch.domain.order.constant;

public enum OrderStatus {

    INIT,
    CANCELED;

    public static OrderStatus from(String orderStatus) {
        return OrderStatus.valueOf(orderStatus.toUpperCase());
    }

    public boolean isCanceled() {
        return this == CANCELED;
    }
}
