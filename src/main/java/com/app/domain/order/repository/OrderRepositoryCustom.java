package com.app.domain.order.repository;

import com.app.api.order.dto.request.OrderSearchCondition;
import com.app.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<Order> findAllBySearchCondition(OrderSearchCondition searchCondition, Pageable pageable);
}
