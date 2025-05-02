package com.app.api.order.controller;

import com.app.api.order.dto.request.OrderCreateRequest;
import com.app.api.order.dto.response.OrderResponse;
import com.app.api.order.service.OrderService;
import com.app.global.resolver.MemberInfo;
import com.app.global.resolver.MemberInfoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
