package com.app.domain.orderProduct.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.order.entity.Order;
import com.app.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class OrderProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_product_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int orderQuantity;

    @Builder
    private OrderProduct(Product product, int orderQuantity) {
        this.product = product;
        this.orderPrice = product.getPrice();
        this.orderQuantity = orderQuantity;
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
}
