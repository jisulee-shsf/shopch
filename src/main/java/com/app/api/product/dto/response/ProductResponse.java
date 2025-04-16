package com.app.api.product.dto.response;

import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.constant.ProductType;
import com.app.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private ProductType productType;
    private ProductSellingStatus productSellingStatus;
    private int price;
    private int stockQuantity;

    @Builder
    private ProductResponse(Long id, String name, ProductType productType, ProductSellingStatus productSellingStatus, int price, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.productType = productType;
        this.productSellingStatus = productSellingStatus;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .productType(product.getProductType())
                .productSellingStatus(product.getProductSellingStatus())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
