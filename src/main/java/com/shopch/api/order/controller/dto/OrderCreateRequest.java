package com.shopch.api.order.controller.dto;

import com.shopch.api.order.service.dto.request.OrderCreateServiceRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "상품 아이디는 필수입니다.")
    @Positive(message = "상품 아이디는 양수여야 합니다.")
    private Long productId;

    @NotNull(message = "주문 수량은 필수입니다.")
    @Positive(message = "주문 수량은 양수여야 합니다.")
    private Integer orderQuantity;

    @Builder
    private OrderCreateRequest(Long productId, Integer orderQuantity) {
        this.productId = productId;
        this.orderQuantity = orderQuantity;
    }

    public OrderCreateServiceRequest toServiceRequest() {
        return OrderCreateServiceRequest.builder()
                .productId(productId)
                .orderQuantity(orderQuantity)
                .build();
    }
}
