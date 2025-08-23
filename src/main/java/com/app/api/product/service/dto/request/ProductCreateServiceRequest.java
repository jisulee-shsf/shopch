package com.app.api.product.service.dto.request;

import com.app.domain.product.constant.ProductType;
import com.app.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductCreateServiceRequest {

    private final String name;
    private final ProductType productType;
    private final Integer price;
    private final Integer stockQuantity;

    @Builder
    private ProductCreateServiceRequest(String name, ProductType productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Product toProduct() {
        return Product.create(name, productType, price, stockQuantity);
    }
}
