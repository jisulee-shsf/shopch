package com.app.domain.orderProduct.entity;

import com.app.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    @DisplayName("주문 상품 생성 시 주문 상품이 상품을 참조하는 단방향 연관관계가 설정된다.")
    @Test
    void create() {
        // given
        Product product = createTestProduct();

        // when
        OrderProduct orderProduct = OrderProduct.create(product, 1);

        // then
        assertThat(orderProduct.getProduct()).isEqualTo(product);
    }

    private Product createTestProduct() {
        return Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .price(10000)
                .stockQuantity(1)
                .build();
    }
}
