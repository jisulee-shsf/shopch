package com.shopch.domain.product.entity;

import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.domain.common.BaseEntity;
import com.shopch.domain.product.constant.ProductSellingStatus;
import com.shopch.domain.product.constant.ProductType;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@BatchSize(size = 100)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ProductType productType;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ProductSellingStatus productSellingStatus;

    private int price;
    private int stockQuantity;

    @Builder
    private Product(String name, ProductType productType, ProductSellingStatus productSellingStatus, int price, int stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.productSellingStatus = productSellingStatus;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public static Product create(String name, ProductType productType, int price, int stockQuantity) {
        return Product.builder()
                .name(name)
                .productType(productType)
                .productSellingStatus(ProductSellingStatus.SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    public void update(ProductUpdateServiceRequest request) {
        name = request.getName();
        productType = request.getProductType();
        price = request.getPrice();
        stockQuantity = request.getStockQuantity();
        changeProductSellingStatus();
    }

    public void deductStockQuantity(int orderQuantity) {
        if (isStockQuantityLessThanOrderQuantity(orderQuantity)) {
            throw new OutOfStockException(ErrorCode.OUT_OF_STOCK);
        }
        stockQuantity -= orderQuantity;
        changeProductSellingStatus();
    }

    public void addStockQuantity(int orderQuantity) {
        stockQuantity += orderQuantity;
        changeProductSellingStatus();
    }

    private void changeProductSellingStatus() {
        productSellingStatus = (stockQuantity == 0) ? ProductSellingStatus.COMPLETED : ProductSellingStatus.SELLING;
    }

    private boolean isStockQuantityLessThanOrderQuantity(int orderQuantity) {
        return stockQuantity < orderQuantity;
    }
}
