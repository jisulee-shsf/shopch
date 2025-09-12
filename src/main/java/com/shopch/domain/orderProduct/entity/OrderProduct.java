package com.shopch.domain.orderProduct.entity;

import com.shopch.domain.common.BaseEntity;
import com.shopch.domain.order.entity.Order;
import com.shopch.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int orderPrice;
    private int orderQuantity;

    @Builder
    private OrderProduct(Product product, int orderQuantity) {
        this.product = product;
        this.orderPrice = product.getPrice();
        this.orderQuantity = orderQuantity;
        product.deductStockQuantity(orderQuantity);
    }

    public static OrderProduct create(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
    }

    public void assignOrder(Order order) {
        this.order = order;
    }

    public void restoreStockQuantity() {
        product.addStockQuantity(orderQuantity);
    }

    public int calculateSubTotalPrice() {
        return orderPrice * orderQuantity;
    }
}
