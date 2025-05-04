package com.app.domain.orderProduct.entity;

import com.app.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    @DisplayName("주문 상품 생성 시 주문 가격은 상품 가격과 같다.")
    @Test
    void create_orderPrice() {
        // given
        Product product = createTestProduct(10000, 1);

        // when
        OrderProduct orderProduct = OrderProduct.create(product, 1);

        // then
        assertThat(orderProduct.getOrderPrice()).isEqualTo(10000);
    }

    @DisplayName("주문 상품 생성 시 상품 재고 수량을 주문 수량만큼 차감한다.")
    @Test
    void create_deductStockQuantity() {
        // given
        Product product = createTestProduct(10000, 1);

        // when
        OrderProduct.create(product, 1);

        // then
        assertThat(product.getStockQuantity()).isZero();
    }

    @DisplayName("주문 상품의 총 금액은 주문 가격(상품 가격) * 주문 수량이다.")
    @Test
    void calculateTotalPrice() {
        // given
        Product product = createTestProduct(10000, 2);
        OrderProduct orderProduct = createTestOrderProduct(product, 2);

        // when
        int totalPrice = orderProduct.calculateTotalPrice();

        // then
        assertThat(totalPrice).isEqualTo(20000);
    }

    @DisplayName("주문 상품 취소 시 상품 재고 수량을 주문 수량만큼 복구한다.")
    @Test
    void cancel() {
        // given
        Product product = createTestProduct(10000, 1);
        OrderProduct orderProduct = createTestOrderProduct(product, 1);

        // when
        orderProduct.cancel();

        // then
        assertThat(product.getStockQuantity()).isEqualTo(1);
    }

    private Product createTestProduct(int price, int stockQuantity) {
        return Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    private OrderProduct createTestOrderProduct(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
    }
}
