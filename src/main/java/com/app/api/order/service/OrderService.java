package com.app.api.order.service;

import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.response.OrderResponse;
import com.app.api.product.service.ProductService;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.domain.order.entity.Order;
import com.app.domain.order.repository.OrderRepository;
import com.app.domain.orderProduct.entity.OrderProduct;
import com.app.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final MemberService memberService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(Long memberId, LocalDateTime now, OrderCreateRequest request) {
        Member member = memberService.findMemberById(memberId);
        Product product = productService.findProductById(request.getProductId());

        List<OrderProduct> orderProducts = new ArrayList<>();
        OrderProduct orderProduct = OrderProduct.create(product, request.getOrderQuantity());
        orderProducts.add(orderProduct);

        Order order = Order.create(member, now, orderProducts);
        orderRepository.save(order);
        return OrderResponse.of(order);
    }
}
