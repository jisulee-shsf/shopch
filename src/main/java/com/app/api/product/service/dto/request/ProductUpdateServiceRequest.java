package com.app.api.product.service.dto.request;

import com.app.domain.product.constant.ProductType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductUpdateServiceRequest {

    private String name;
    private ProductType productType;
    private Integer price;
    private Integer stockQuantity;

    @Builder
    private ProductUpdateServiceRequest(String name, ProductType productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
