package com.app.api.product.dto.request;

import com.app.domain.product.constant.ProductType;
import com.app.domain.product.entity.Product;
import com.app.global.validator.ValidEnum;
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
    @ValidEnum(enumClass = ProductType.class, message = "유효한 상품 타입이 아닙니다.")
    private String productType;

    @NotNull(message = "등록 상품 가격은 필수입니다.")
    @Positive(message = "등록 상품 가격은 양수여야 합니다.")
    private Integer price;

    @NotNull(message = "등록 상품 재고 수량은 필수입니다.")
    @Positive(message = "등록 상품 재고 수량은 양수여야 합니다.")
    private Integer stockQuantity;

    @Builder
    private ProductCreateRequest(String name, String productType, Integer price, Integer stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Product toEntity() {
        return Product.create(name, ProductType.from(productType), price, stockQuantity);
    }
}
