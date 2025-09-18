package com.shopch.domain.order.entity;

import com.shopch.domain.common.BaseEntity;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.order.constant.OrderStatus;
import com.shopch.domain.orderProduct.entity.OrderProduct;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @BatchSize(size = 100)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    private int totalPrice;

    @Builder
    private Order(Member member, OrderStatus orderStatus, LocalDateTime orderedAt, List<OrderProduct> orderProducts) {
        this.member = member;
        this.orderStatus = orderStatus;
        this.orderedAt = orderedAt;
        totalPrice = sumTotalPrice(orderProducts);
        orderProducts.forEach(this::addOrderProduct);
    }

    public static Order create(Member member, LocalDateTime orderedAt, List<OrderProduct> orderProducts) {
        return Order.builder()
                .member(member)
                .orderStatus(OrderStatus.INIT)
                .orderedAt(orderedAt)
                .orderProducts(orderProducts)
                .build();
    }

    public void cancel() {
        if (orderStatus.isCanceled()) {
            throw new BusinessException(ErrorCode.ALREADY_CANCELED_ORDER);
        }
        orderStatus = OrderStatus.CANCELED;
        orderProducts.forEach(OrderProduct::restoreStockQuantity);
    }

    public boolean isOwner(Long memberId) {
        return member.hasSameId(memberId);
    }

    public boolean isNotOwner(Long memberId) {
        return !isOwner(memberId);
    }

    private void addOrderProduct(OrderProduct orderProduct) {
        orderProducts.add(orderProduct);
        orderProduct.assignOrder(this);
    }

    private int sumTotalPrice(List<OrderProduct> orderProducts) {
        return orderProducts.stream()
                .mapToInt(OrderProduct::calculateSubTotalPrice)
                .sum();
    }
}
