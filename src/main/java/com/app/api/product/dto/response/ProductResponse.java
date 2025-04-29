package com.app.api.product.dto.response;

import com.app.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProductResponse {

    private Long id;
    private String name;
    private String productType;
    private String productSellingStatus;
    private int price;
    private int stockQuantity;

    @Builder
    private ProductResponse(Long id, String name, String productType, String productSellingStatus, int price, int stockQuantity) {
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
                .productType(product.getProductType().name())
                .productSellingStatus(product.getProductSellingStatus().name())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
