package com.shopch.domain.order.repository;

import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> findAllBySearchCondition(OrderServiceSearchCondition searchCondition, Pageable pageable);
}
