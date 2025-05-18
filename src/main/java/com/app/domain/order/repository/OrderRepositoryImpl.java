package com.app.domain.order.repository;

import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.domain.order.constant.OrderStatus;
import com.app.domain.order.entity.Order;
import com.app.domain.order.entity.QOrder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> findAllBySearchCondition(OrderServiceSearchCondition searchCondition, Pageable pageable) {
        List<Order> content = jpaQueryFactory
                .selectFrom(QOrder.order)
                .where(
                        memberNameEq(searchCondition.getMemberName()),
                        orderStatusEq(searchCondition.getOrderStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(QOrder.order.count())
                .from(QOrder.order)
                .where(
                        memberNameEq(searchCondition.getMemberName()),
                        orderStatusEq(searchCondition.getOrderStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression memberNameEq(String memberName) {
        return StringUtils.hasText(memberName) ? QOrder.order.member.name.eq(memberName) : null;
    }

    private BooleanExpression orderStatusEq(String orderStatus) {
        return StringUtils.hasText(orderStatus) ? QOrder.order.orderStatus.eq(OrderStatus.from(orderStatus)) : null;
    }
}
