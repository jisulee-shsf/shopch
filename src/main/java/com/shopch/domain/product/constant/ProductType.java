package com.shopch.domain.product.constant;

public enum ProductType {

    PRODUCT_1,
    PRODUCT_2;

    public static ProductType from(String productType) {
        return ProductType.valueOf(productType.toUpperCase());
    }
}
