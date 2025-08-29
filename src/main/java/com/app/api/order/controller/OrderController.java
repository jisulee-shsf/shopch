package com.app.api.order.controller;

import com.app.api.common.PageResponse;
import com.app.api.order.controller.dto.request.OrderCreateRequest;
import com.app.api.order.controller.dto.request.OrderSearchCondition;
import com.app.api.order.service.OrderService;
import com.app.api.order.service.dto.response.OrderResponse;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@MemberInfo MemberInfoDto memberInfo,
                                                     @Valid @RequestBody OrderCreateRequest request) {
        LocalDateTime orderedAt = LocalDateTime.now();
        return ResponseEntity.ok(orderService.createOrder(memberInfo.getId(), orderedAt, request.toServiceRequest()));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@MemberInfo MemberInfoDto memberInfo,
                                            @PathVariable Long orderId) {
        orderService.cancelOrder(memberInfo.getId(), orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> findOrders(@Valid OrderSearchCondition searchCondition,
                                                                  Pageable pageable) {
        return ResponseEntity.ok(orderService.findOrders(searchCondition.toServiceSearchCondition(), pageable));
    }
}
