package com.app.domain.order.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.member.entity.Member;
import com.app.domain.order.constant.OrderStatus;
import com.app.domain.orderProduct.entity.OrderProduct;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.app.domain.order.constant.OrderStatus.INIT;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private int totalPrice;

    @Builder
    private Order(Member member, LocalDateTime orderDateTime, OrderStatus orderStatus, List<OrderProduct> orderProducts) {
        this.member = member;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.orderProducts = orderProducts;
        this.totalPrice = getTotalPrice(orderProducts);
    }

    public static Order create(Member member, LocalDateTime orderDateTime, List<OrderProduct> orderProducts) {
        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(INIT)
                .orderProducts(orderProducts)
                .build();
    }

    private int getTotalPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(OrderProduct::calculateTotalPrice)
                .sum();
    }
}
