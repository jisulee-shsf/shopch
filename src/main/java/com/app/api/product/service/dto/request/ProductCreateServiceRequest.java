package com.app.api.product.service.dto.request;

import com.app.domain.product.constant.ProductType;
import com.app.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductCreateServiceRequest {

    private String name;
    private String productType;
    private Integer price;
    private Integer stockQuantity;

    @Builder
    private ProductCreateServiceRequest(String name, String productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Product toEntity() {
        return Product.create(name, ProductType.from(productType), price, stockQuantity);
    }
}
