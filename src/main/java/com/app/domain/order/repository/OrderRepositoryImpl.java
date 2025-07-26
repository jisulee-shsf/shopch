package com.app.domain.order.repository;

import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.domain.order.constant.OrderStatus;
import com.app.domain.order.entity.Order;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.app.domain.member.entity.QMember.member;
import static com.app.domain.order.entity.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Order> findAllBySearchCondition(OrderServiceSearchCondition searchCondition, Pageable pageable) {
        List<Order> content = jpaQueryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .where(
                        memberNameEq(searchCondition.getMemberName()),
                        orderStatusEq(searchCondition.getOrderStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = jpaQueryFactory
                .select(order.count())
                .from(order)
                .where(
                        memberNameEq(searchCondition.getMemberName()),
                        orderStatusEq(searchCondition.getOrderStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression memberNameEq(String memberName) {
        return StringUtils.hasText(memberName) ? order.member.name.eq(memberName) : null;
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus != null ? order.orderStatus.eq(orderStatus) : null;
    }
}
