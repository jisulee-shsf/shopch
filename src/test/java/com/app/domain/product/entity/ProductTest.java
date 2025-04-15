package com.app.domain.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @DisplayName("상품 생성 시 상품 판매 상태는 SELLING이다.")
    @Test
    void create() {
        // given
        // when
        Product product = Product.create("product", PRODUCT_A, 10000, 1);

        // then
        assertThat(product.getProductSellingStatus()).isEqualTo(SELLING);
    }
}
