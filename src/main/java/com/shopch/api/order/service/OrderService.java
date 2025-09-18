package com.shopch.api.order.service;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.order.service.dto.request.OrderCreateServiceRequest;
import com.shopch.api.order.service.dto.request.OrderServiceSearchCondition;
import com.shopch.api.order.service.dto.response.OrderResponse;
import com.shopch.api.product.service.ProductService;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.service.MemberService;
import com.shopch.domain.order.entity.Order;
import com.shopch.domain.order.repository.OrderRepository;
import com.shopch.domain.orderProduct.entity.OrderProduct;
import com.shopch.domain.product.entity.Product;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthException;
import com.shopch.global.error.exception.EntityNotFoundException;
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
    public OrderResponse createOrder(Long memberId, OrderCreateServiceRequest request, LocalDateTime orderedAt) {
        Product product = productService.getProduct(request.getProductId());
        OrderProduct orderProduct = OrderProduct.create(product, request.getOrderQuantity());

        List<OrderProduct> orderProducts = new ArrayList<>();
        orderProducts.add(orderProduct);

        Member member = memberService.getMember(memberId);
        Order order = Order.create(member, orderedAt, orderProducts);

        return OrderResponse.of(orderRepository.save(order));
    }

    @Transactional
    public void cancelOrder(Long memberId, Long orderId) {
        Order order = getOrder(orderId);
        validateOwner(order, memberId);

        order.cancel();
    }

    public PageResponse<OrderResponse> searchOrders(OrderServiceSearchCondition searchCondition, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllBySearchCondition(searchCondition, pageable);
        return PageResponse.of(orders.map(OrderResponse::of));
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));
    }

    private void validateOwner(Order order, Long memberId) {
        if (order.isNotOwner(memberId)) {
            throw new AuthException(ErrorCode.ORDER_ACCESS_DENIED);
        }
    }
}
