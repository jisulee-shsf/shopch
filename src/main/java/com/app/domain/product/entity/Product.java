package com.app.domain.product.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.constant.ProductType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private ProductType type;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private ProductSellingStatus sellingStatus;

    private int price;
    private int stockQuantity;

    @Builder
    private Product(String name, ProductType type, ProductSellingStatus sellingStatus, int price, int stockQuantity) {
        this.name = name;
        this.type = type;
        this.sellingStatus = sellingStatus;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public static Product create(String name, ProductType type, int price, int stockQuantity) {
        return Product.builder()
                .name(name)
                .type(type)
                .sellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}
