package com.shopch.api.product.controller.dto;

import com.shopch.api.product.service.dto.request.ProductCreateServiceRequest;
import com.shopch.domain.product.constant.ProductType;
import com.shopch.global.validator.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "상품 타입은 필수입니다.")
    @ValueOfEnum(enumClass = ProductType.class, message = "유효하지 않은 상품 타입입니다.")
    private String productType;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Positive(message = "상품 가격은 양수여야 합니다.")
    private Integer price;

    @NotNull(message = "상품 재고 수량은 필수입니다.")
    @Positive(message = "상품 등록 시 재고 수량은 양수여야 합니다.")
    private Integer stockQuantity;

    @Builder
    private ProductCreateRequest(String name, String productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public ProductCreateServiceRequest toServiceRequest() {
        return ProductCreateServiceRequest.builder()
                .name(name)
                .productType(ProductType.from(productType))
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}
