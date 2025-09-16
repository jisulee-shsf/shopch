package com.shopch.api.product.service.dto.request;

import com.shopch.domain.product.constant.ProductType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductUpdateServiceRequest {

    private final String name;
    private final ProductType productType;
    private final Integer price;
    private final Integer stockQuantity;

    @Builder
    private ProductUpdateServiceRequest(String name, ProductType productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
