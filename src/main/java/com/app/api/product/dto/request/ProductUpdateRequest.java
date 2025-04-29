package com.app.api.product.dto.request;

import com.app.domain.product.constant.ProductType;
import com.app.global.validator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "상품 타입은 필수입니다.")
    @ValidEnum(enumClass = ProductType.class, message = "유효한 상품 타입이 아닙니다.")
    private String productType;

    @Positive(message = "수정 상품 가격은 양수여야 합니다.")
    private int price;

    @PositiveOrZero(message = "수정 상품 재고는 0 이상이어야 합니다.")
    private int stockQuantity;

    @Builder
    private ProductUpdateRequest(String name, String productType, int price, int stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
