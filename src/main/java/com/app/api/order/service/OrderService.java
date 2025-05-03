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
import com.app.global.error.exception.EntityNotFoundException;
import com.app.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.app.global.error.ErrorType.FORBIDDEN_ORDER_CANCELLATION;
import static com.app.global.error.ErrorType.ORDER_NOT_FOUND;

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

    @Transactional
    public void cancelOrder(Long memberId, Long orderId) {
        Member member = memberService.findMemberById(memberId);
        Order order = findOrderById(orderId);
        validateMember(member, order.getMember());

        order.cancel();
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(ORDER_NOT_FOUND));
    }

    private void validateMember(Member member, Member orderedMember) {
        if (!member.isSameId(orderedMember.getId())) {
            throw new ForbiddenException(FORBIDDEN_ORDER_CANCELLATION);
        }
    }
}
