package com.app.domain.order.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.order.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
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

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    private int totalPrice;
}
