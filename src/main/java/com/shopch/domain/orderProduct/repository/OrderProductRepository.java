package com.shopch.domain.orderProduct.repository;

import com.shopch.domain.orderProduct.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
}
