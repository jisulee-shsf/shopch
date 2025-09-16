package com.shopch.domain.product.entity;

import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.global.error.exception.InsufficientStockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shopch.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_2;
import static com.shopch.global.error.ErrorCode.INSUFFICIENT_STOCK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private static final String PRODUCT_1_NAME = "product1";
    private static final int PRODUCT_1_PRICE = 10000;
    private static final int PRODUCT_1_STOCK_QUANTITY = 10;
    private static final String PRODUCT_2_NAME = "product2";
    private static final int PRODUCT_2_PRICE = 20000;
    private static final int PRODUCT_2_STOCK_QUANTITY = 20;
    private static final int ZERO_STOCK_QUANTITY = 0;
    private static final int MINIMUM_ORDER_QUANTITY = 1;

    @DisplayName("상품 생성 시 상품 판매 상태는 SELLING이다.")
    @Test
    void create() {
        // when
        Product product = Product.create(PRODUCT_1_NAME, PRODUCT_1, PRODUCT_1_PRICE, PRODUCT_1_STOCK_QUANTITY);

        // then
        assertThat(product)
                .extracting(
                        Product::getName,
                        Product::getProductType,
                        Product::getProductSellingStatus,
                        Product::getPrice,
                        Product::getStockQuantity
                ).
                containsExactly(
                        PRODUCT_1_NAME,
                        PRODUCT_1,
                        SELLING,
                        PRODUCT_1_PRICE,
                        PRODUCT_1_STOCK_QUANTITY
                );
    }

    @DisplayName("상품 정보를 주어진 상품 정보로 변경한다.")
    @Test
    void update() {
        // given
        Product product = Product.builder()
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1)
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2)
                .price(PRODUCT_2_PRICE)
                .stockQuantity(PRODUCT_2_STOCK_QUANTITY)
                .build();

        // when
        product.update(request);

        // then
        assertThat(product)
                .extracting(
                        Product::getName,
                        Product::getProductType,
                        Product::getPrice,
                        Product::getStockQuantity
                )
                .containsExactly(
                        PRODUCT_2_NAME,
                        PRODUCT_2,
                        PRODUCT_2_PRICE,
                        PRODUCT_2_STOCK_QUANTITY
                );
    }

    @DisplayName("변경한 재고 수량이 0일 경우, 상품 판매 상태를 SELLING에서 COMPLETED로 변경한다.")
    @Test
    void update_OutOfStock() {
        // given
        Product product = createProduct(PRODUCT_1_STOCK_QUANTITY);

        ProductUpdateServiceRequest request = createProductUpdateServiceRequest(ZERO_STOCK_QUANTITY);

        // when
        product.update(request);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(ZERO_STOCK_QUANTITY);
        assertThat(product.getProductSellingStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("재고 수량을 주어진 주문 수량만큼 차감한다.")
    @Test
    void deductStockQuantity() {
        // given
        Product product = createProduct(PRODUCT_1_STOCK_QUANTITY + MINIMUM_ORDER_QUANTITY);

        // when
        product.deductStockQuantity(MINIMUM_ORDER_QUANTITY);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(PRODUCT_1_STOCK_QUANTITY);
    }

    @DisplayName("재고 수량이 주문 수량보다 적을 때 재고 수량 차감을 시도할 경우, 예외가 발생한다.")
    @Test
    void deductStockQuantity_InsufficientStock() {
        // given
        Product product = createProduct(PRODUCT_1_STOCK_QUANTITY);

        // when & then
        assertThatThrownBy(() -> product.deductStockQuantity(PRODUCT_1_STOCK_QUANTITY + MINIMUM_ORDER_QUANTITY))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage(INSUFFICIENT_STOCK.getMessage());
    }

    @DisplayName("차감한 재고 수량이 0일 경우, 상품 판매 상태를 SELLING에서 COMPLETED로 변경한다.")
    @Test
    void deductStockQuantity_OutOfStock() {
        // given
        Product product = createProduct(PRODUCT_1_STOCK_QUANTITY);

        // when
        product.deductStockQuantity(PRODUCT_1_STOCK_QUANTITY);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(ZERO_STOCK_QUANTITY);
        assertThat(product.getProductSellingStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("재고 수량을 주문 수량만큼 더한다.")
    @Test
    void addStockQuantity() {
        // given
        Product product = createProduct(PRODUCT_1_STOCK_QUANTITY);

        // when
        product.addStockQuantity(MINIMUM_ORDER_QUANTITY);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(PRODUCT_1_STOCK_QUANTITY + MINIMUM_ORDER_QUANTITY);
    }

    @DisplayName("재고 수량이 생길 경우, 상품 판매 상태를 COMPLETED에서 SELLING으로 변경한다.")
    @Test
    void addStockQuantity_BackInStock() {
        // given
        Product product = createProduct(ZERO_STOCK_QUANTITY);

        // when
        product.addStockQuantity(MINIMUM_ORDER_QUANTITY);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(MINIMUM_ORDER_QUANTITY);
        assertThat(product.getProductSellingStatus()).isEqualTo(SELLING);
    }

    private Product createProduct(int stockQuantity) {
        return Product.builder()
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1)
                .productSellingStatus(SELLING)
                .price(PRODUCT_1_PRICE)
                .stockQuantity(stockQuantity)
                .build();
    }

    private ProductUpdateServiceRequest createProductUpdateServiceRequest(int stockQuantity) {
        return ProductUpdateServiceRequest.builder()
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2)
                .price(PRODUCT_2_PRICE)
                .stockQuantity(stockQuantity)
                .build();
    }
}
