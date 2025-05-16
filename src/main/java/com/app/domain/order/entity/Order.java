package com.app.domain.order.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.member.entity.Member;
import com.app.domain.order.constant.OrderStatus;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.app.global.error.ErrorType.ALREADY_CANCELED_ORDER;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private int totalOrderPrice;

    @Builder
    private Order(Member member, LocalDateTime orderDateTime, OrderStatus orderStatus, List<OrderProduct> orderProducts) {
        this.member = member;
        this.orderDateTime = orderDateTime;
        this.orderStatus = orderStatus;
        this.totalOrderPrice = getTotalOrderPrice(orderProducts);
        orderProducts.forEach(this::changeOrderProduct);
    }

    public static Order create(Member member, LocalDateTime orderDateTime, List<OrderProduct> orderProducts) {
        return Order.builder()
                .member(member)
                .orderDateTime(orderDateTime)
                .orderStatus(OrderStatus.INIT)
                .orderProducts(orderProducts)
                .build();
    }

    public void cancel() {
        if (isAlreadyCanceled()) {
            throw new BusinessException(ALREADY_CANCELED_ORDER);
        }
        orderStatus = OrderStatus.CANCELED;
        orderProducts.forEach(OrderProduct::cancel);
    }

    private int getTotalOrderPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(OrderProduct::calculateTotalPrice)
                .sum();
    }

    private void changeOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.changeOrder(this);
    }

    private boolean isAlreadyCanceled() {
        return orderStatus == OrderStatus.CANCELED;
    }
}
