package com.app.api.product.controller.dto.request;

import com.app.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.app.domain.product.constant.ProductType;
import com.app.global.validator.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @EnumValue(enumClass = ProductType.class, message = "유효하지 않은 상품 타입입니다.")
    private String productType;

    @NotNull(message = "수정 상품 가격은 필수입니다.")
    @Positive(message = "수정 상품 가격은 양수여야 합니다.")
    private Integer price;

    @NotNull(message = "수정 상품 재고 수량은 필수입니다.")
    @PositiveOrZero(message = "수정 상품 재고 수량은 0 이상이어야 합니다.")
    private Integer stockQuantity;

    @Builder
    private ProductUpdateRequest(String name, String productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public ProductUpdateServiceRequest toServiceRequest() {
        return ProductUpdateServiceRequest.builder()
                .name(name)
                .productType(ProductType.from(productType))
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}
