package com.app.api.product.service.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductUpdateServiceRequest {

    private String name;
    private String productType;
    private Integer price;
    private Integer stockQuantity;

    @Builder
    private ProductUpdateServiceRequest(String name, String productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
