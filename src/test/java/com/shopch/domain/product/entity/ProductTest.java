package com.shopch.domain.product.entity;

import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.global.error.exception.OutOfStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shopch.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_A;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_B;
import static com.shopch.global.error.ErrorCode.OUT_OF_STOCK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("생성된 상품 정보를 주어진 상품 정보로 변경한다.")
    @Test
    void update() {
        // given
        Product product = Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .price(10000)
                .stockQuantity(1)
                .build();

        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B)
                .price(20000)
                .stockQuantity(2)
                .build();

        // when
        product.update(request);

        // then
        assertThat(product)
                .extracting("name", "productType", "price", "stockQuantity")
                .containsExactly("updatedProduct", PRODUCT_B, 20000, 2);
    }

    @DisplayName("변경한 상품 재고 수량이 0이 될 경우, 상품 판매 상태를 SELLING에서 COMPLETED로 변경한다.")
    @Test
    void update_ZeroStockQuantity() {
        // given
        Product product = Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(10000)
                .stockQuantity(1)
                .build();

        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B)
                .price(20000)
                .stockQuantity(0)
                .build();

        // when
        product.update(request);

        // then
        assertThat(product.getStockQuantity()).isZero();
        assertThat(product.getProductSellingStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("상품 재고 수량을 주문 수량만큼 차감한다.")
    @Test
    void deductStockQuantity() {
        // given
        Product product = createTestProduct(1);

        // when
        product.deductStockQuantity(1);

        // then
        assertThat(product.getStockQuantity()).isZero();
    }

    @DisplayName("상품 재고 수량이 주문 수량보다 적을 때 차감을 시도할 경우, 예외가 발생한다.")
    @Test
    void deductStockQuantity_StockQuantityLessThanOrderQuantity() {
        // given
        Product product = createTestProduct(1);

        // when & then
        assertThatThrownBy(() -> product.deductStockQuantity(2))
                .isInstanceOf(OutOfStockException.class)
                .hasMessage(OUT_OF_STOCK.getMessage());
    }

    @DisplayName("차감한 상품 재고 수량이 0이 될 경우, 상품 판매 상태를 SELLING에서 COMPLETED로 변경한다.")
    @Test
    void deductStockQuantity_ZeroStockQuantity() {
        // given
        Product product = createTestProduct(1);

        // when
        product.deductStockQuantity(1);

        // then
        assertThat(product.getProductSellingStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("상품 재고 수량을 주문 수량만큼 복구한다.")
    @Test
    void addStockQuantity() {
        // given
        Product product = createTestProduct(0);

        // when
        product.addStockQuantity(1);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(1);

    }

    @DisplayName("복구한 상품 재고 수량이 양수가 될 경우, 상품 판매 상태를 COMPLETED에서 SELLING으로 변경한다.")
    @Test
    void addStockQuantity_PositiveStockQuantity() {
        // given
        Product product = createTestProduct(0);

        // when
        product.addStockQuantity(1);

        // then
        assertThat(product.getProductSellingStatus()).isEqualTo(SELLING);

    }

    private Product createTestProduct(int stockQuantity) {
        return Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(10000)
                .stockQuantity(stockQuantity)
                .build();
    }
}
