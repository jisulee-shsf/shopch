package com.app.domain.product.constant;

import java.util.List;

public enum ProductSellingStatus {

    SELLING,
    COMPLETED;

    public static List<ProductSellingStatus> forDisplay() {
        return List.of(SELLING);
    }
}
