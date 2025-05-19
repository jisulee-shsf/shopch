package com.app.domain.orderProduct.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.order.entity.Order;
import com.app.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    public void changeOrder(Order order) {
        this.order = order;
    }

    public int calculateTotalPrice() {
        return orderPrice * orderQuantity;
    }

    public void cancel() {
        product.addStockQuantity(orderQuantity);
    }
}
