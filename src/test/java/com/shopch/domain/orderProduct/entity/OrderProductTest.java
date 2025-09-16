package com.shopch.domain.orderProduct.entity;

import com.shopch.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static org.assertj.core.api.Assertions.assertThat;

class OrderProductTest {

    private static final String PRODUCT_NAME = "product";
    private static final int PRODUCT_PRICE = 10000;
    private static final int PRODUCT_STOCK_QUANTITY = 10;
    private static final int MINIMUM_ORDER_QUANTITY = 1;

    @DisplayName("주문 상품 생성 시 주문 금액은 상품 금액과 같다.")
    @Test
    void create() {
        // given
        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);

        // when
        OrderProduct orderProduct = OrderProduct.create(product, MINIMUM_ORDER_QUANTITY);

        // then
        assertThat(orderProduct)
                .extracting(
                        OrderProduct::getProduct,
                        OrderProduct::getOrderPrice,
                        OrderProduct::getOrderQuantity
                ).
                containsExactly(
                        product,
                        PRODUCT_PRICE,
                        MINIMUM_ORDER_QUANTITY
                );
    }

    @DisplayName("주문 상품 생성 시 상품 재고 수량을 주어진 주문 수량만큼 차감한다.")
    @Test
    void create_StockQuantityDeduction() {
        // given
        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);

        // when
        OrderProduct.create(product, MINIMUM_ORDER_QUANTITY);

        // then
        assertThat(product.getStockQuantity()).isEqualTo(PRODUCT_STOCK_QUANTITY - MINIMUM_ORDER_QUANTITY);
    }

    @DisplayName("상품 재고 수량을 주어진 주문 수량만큼 복구한다.")
    @Test
    void restoreStockQuantity() {
        // given
        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        // when
        orderProduct.restoreStockQuantity();

        // then
        assertThat(product.getStockQuantity()).isEqualTo(PRODUCT_STOCK_QUANTITY);
    }

    @DisplayName("주문 상품 금액은 주문 금액(상품 금액) * 주문 수량이다.")
    @Test
    void calculateSubTotalPrice() {
        // given
        Product product = createProduct(PRODUCT_PRICE, PRODUCT_STOCK_QUANTITY);
        OrderProduct orderProduct = createOrderProduct(product, MINIMUM_ORDER_QUANTITY);

        // when
        int subTotalPrice = orderProduct.calculateSubTotalPrice();

        // then
        assertThat(subTotalPrice).isEqualTo(orderProduct.getOrderPrice() * MINIMUM_ORDER_QUANTITY);
        assertThat(subTotalPrice).isEqualTo(PRODUCT_PRICE * MINIMUM_ORDER_QUANTITY);
    }

    private Product createProduct(int price, int stockQuantity) {
        return Product.builder()
                .name(PRODUCT_NAME)
                .productType(PRODUCT_1)
                .productSellingStatus(SELLING)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }

    private OrderProduct createOrderProduct(Product product, int orderQuantity) {
        return OrderProduct.builder()
                .product(product)
                .orderQuantity(orderQuantity)
                .build();
    }
}
