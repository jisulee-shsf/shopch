package com.app.api.order.service;

import com.app.api.common.PageResponse;
import com.app.api.order.service.dto.request.OrderCreateServiceRequest;
import com.app.api.order.service.dto.request.OrderServiceSearchCondition;
import com.app.api.order.service.dto.response.OrderResponse;
import com.app.api.product.service.ProductService;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.domain.order.entity.Order;
import com.app.domain.order.repository.OrderRepository;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.product.entity.Product;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.EntityNotFoundException;
import com.app.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final ProductService productService;
    private final MemberService memberService;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(Long memberId, LocalDateTime orderedAt, OrderCreateServiceRequest request) {
        Product product = productService.getProductById(request.getProductId());
        OrderProduct orderProduct = OrderProduct.create(product, request.getOrderQuantity());

        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        Member member = memberService.getMemberById(memberId);
        Order order = Order.create(member, orderedAt, orderProducts);

        return OrderResponse.of(orderRepository.save(order));
    }

    @Transactional
    public void cancelOrder(Long memberId, Long orderId) {
        Order order = getOrderById(orderId);
        validateOwner(order, memberId);

        order.cancel();
    }

    public PageResponse<OrderResponse> findOrders(OrderServiceSearchCondition searchCondition, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllBySearchCondition(searchCondition, pageable);
        return PageResponse.of(orders.map(OrderResponse::of));
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateOwner(Order order, Long memberId) {
        if (order.isNotOwner(memberId)) {
            throw new ForbiddenException(ErrorCode.ORDER_CANCELLATION_DENIED);
        }
    }
}
