package com.app.domain.order.repository;

import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> findAllBySearchCondition(OrderServiceSearchCondition searchCondition, Pageable pageable);
}
