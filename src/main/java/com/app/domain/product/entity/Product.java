package com.app.domain.product.entity;

import com.app.api.product.dto.request.ProductUpdateRequest;
import com.app.domain.common.BaseEntity;
import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.constant.ProductType;
import com.app.global.error.ErrorType;
import com.app.global.error.exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.app.global.error.ErrorType.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    public void update(ProductUpdateRequest request) {
        name = request.getName();
        productType = ProductType.from(request.getProductType());
        price = request.getPrice();
        stockQuantity = request.getStockQuantity();
        changeProductSellingStatus();
    }

    public void deductStockQuantity(int orderQuantity) {
        if (isStockQuantityLessThanOrderQuantity(orderQuantity)) {
            throw new OutOfStockException(OUT_OF_STOCK);
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
