package com.app.api.order.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "상품 아이디는 필수입니다.")
    private Long productId;

    @Positive(message = "주문 수량은 양수여야 합니다.")
    private int orderQuantity;

    @Builder
    private OrderCreateRequest(Long productId, int orderQuantity) {
        this.productId = productId;
        this.orderQuantity = orderQuantity;
    }
}
