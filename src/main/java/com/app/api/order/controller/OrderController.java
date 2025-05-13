package com.app.api.order.controller;

import com.app.api.common.PageResponse;
import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.request.OrderSearchCondition;
import com.app.api.order.dto.response.OrderResponse;
import com.app.api.order.service.OrderService;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@MemberInfo MemberInfoRequest memberInfoRequest,
                                                     @Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        return ResponseEntity.ok(orderService.createOrder(memberInfoRequest.getId(), LocalDateTime.now(), orderCreateRequest));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@MemberInfo MemberInfoRequest request,
                                            @PathVariable Long orderId) {
        orderService.cancelOrder(request.getId(), orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<PageResponse<OrderResponse>> findOrders(OrderSearchCondition searchCondition,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(orderService.findOrders(searchCondition, pageable));
    }
}
