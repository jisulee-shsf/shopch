package com.app.domain.product.constant;

public enum ProductType {

    PRODUCT_A,
    PRODUCT_B,
    PRODUCT_C;

    public static ProductType from(String productType) {
        return ProductType.valueOf(productType);
    }
}
