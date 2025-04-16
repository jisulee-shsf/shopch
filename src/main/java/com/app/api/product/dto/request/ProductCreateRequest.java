package com.app.api.product.dto.request;

import com.app.domain.product.constant.ProductType;
import com.app.domain.product.entity.Product;
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

    @NotNull(message = "상품 타입은 필수입니다.")
    private ProductType productType;

    @Positive(message = "등록 상품 가격은 양수여야 합니다.")
    private int price;

    @Positive(message = "등록 상품 재고는 양수여야 합니다.")
    private int stockQuantity;

    @Builder
    private ProductCreateRequest(String name, ProductType productType, int price, int stockQuantity) {
        this.name = name;
        this.productType = productType;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Product toEntity() {
        return Product.create(name, productType, price, stockQuantity);
    }
}
