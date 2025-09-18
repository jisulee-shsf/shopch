package com.shopch.api.order.controller;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.order.controller.dto.OrderCreateRequest;
import com.shopch.api.order.controller.dto.OrderSearchCondition;
import com.shopch.api.order.service.OrderService;
import com.shopch.api.order.service.dto.response.OrderResponse;
import com.shopch.global.resolver.MemberInfo;
import com.shopch.global.resolver.dto.MemberInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@MemberInfo MemberInfoDto memberInfo,
                                                     @Valid @RequestBody OrderCreateRequest request) {
        LocalDateTime orderedAt = LocalDateTime.now();
        return ResponseEntity.ok(orderService.createOrder(memberInfo.getId(), request.toServiceRequest(), orderedAt));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@MemberInfo MemberInfoDto memberInfo,
                                            @PathVariable Long orderId) {
        orderService.cancelOrder(memberInfo.getId(), orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> searchOrders(@Valid OrderSearchCondition searchCondition,
                                                                    Pageable pageable) {
        return ResponseEntity.ok(orderService.searchOrders(searchCondition.toServiceSearchCondition(), pageable));
    }
}
