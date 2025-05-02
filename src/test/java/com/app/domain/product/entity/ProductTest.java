package com.app.domain.product.entity;

import com.app.api.product.dto.request.ProductUpdateRequest;
import com.app.global.error.ErrorType;
import com.app.global.error.exception.OutOfStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.domain.product.constant.ProductType.PRODUCT_B;
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
                .productSellingStatus(SELLING)
                .price(10000)
                .stockQuantity(1)
                .build();

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(20000)
                .stockQuantity(2)
                .build();

        // when
        product.update(request);

        // then
        assertThat(product)
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("updatedProduct", PRODUCT_B, SELLING, 20000, 2);
    }

    @DisplayName("변경한 상품 재고가 0일 경우, 판매 상태를 SELLING에서 COMPLETED로 변경한다.")
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

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(20000)
                .stockQuantity(0)
                .build();

        // when
        product.update(request);

        // then
        assertThat(product.getStockQuantity()).isZero();
        assertThat(product.getProductSellingStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("상품 재고를 주문 수량만큼 차감한다.")
    @Test
    void deductStockQuantity() {
        // given
        Product product = createTestProduct(2);

        // when
        product.deductStockQuantity(1);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(1);
    }

    @DisplayName("상품 재고가 주문 수량보다 적을 때 차감을 시도할 경우, 예외가 발생한다.")
    @Test
    void deductStockQuantity_StockQuantityLessThanOrderQuantity() {
        // given
        Product product = createTestProduct(1);

        // when & then
        assertThatThrownBy(() -> product.deductStockQuantity(2))
                .isInstanceOf(OutOfStockException.class)
                .hasMessage(ErrorType.OUT_OF_STOCK.getErrorMessage());
    }

    @DisplayName("차감한 상품 재고가 0일 경우, 판매 상태를 SELLING에서 COMPLETED로 변경한다.")
    @Test
    void deductStockQuantity_ZeroStockQuantity() {
        // given
        Product product = createTestProduct(1);

        // when
        product.deductStockQuantity(1);

        // then
        assertThat(product.getProductSellingStatus()).isEqualTo(COMPLETED);
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
