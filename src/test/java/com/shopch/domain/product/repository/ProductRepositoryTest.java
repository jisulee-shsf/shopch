package com.shopch.domain.product.repository;

import com.shopch.domain.product.constant.ProductSellingStatus;
import com.shopch.domain.product.constant.ProductType;
import com.shopch.domain.product.entity.Product;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.shopch.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ProductRepositoryTest extends IntegrationTestSupport {

    private static final String PRODUCT_1_NAME = "product1";
    private static final Integer PRODUCT_1_PRICE = 10000;
    private static final Integer PRODUCT_1_STOCK_QUANTITY = 10;
    private static final String PRODUCT_2_NAME = "product2";
    private static final Integer PRODUCT_2_PRICE = 20000;
    private static final int ZERO_STOCK_QUANTITY = 0;
    private static final int EXPECTED_SIZE = 1;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("판매 상품과 페이징 정보를 조회한다.")
    @Test
    void findAllByProductSellingStatusIn() {
        // given
        Product product1 = createProduct(PRODUCT_1_NAME, PRODUCT_1, SELLING, PRODUCT_1_PRICE, PRODUCT_1_STOCK_QUANTITY);
        Product product2 = createProduct(PRODUCT_2_NAME, PRODUCT_2, COMPLETED, PRODUCT_2_PRICE, ZERO_STOCK_QUANTITY);
        productRepository.saveAll(List.of(product1, product2));

        // when
        Page<Product> products = productRepository.findAllByProductSellingStatusIn(List.of(SELLING), PageRequest.of(0, 2));

        // then
        assertThat(products.getContent()).hasSize(EXPECTED_SIZE)
                .extracting(
                        Product::getName,
                        Product::getProductType,
                        Product::getProductSellingStatus,
                        Product::getPrice,
                        Product::getStockQuantity
                )
                .containsExactly(
                        tuple(
                                PRODUCT_1_NAME,
                                PRODUCT_1,
                                SELLING,
                                PRODUCT_1_PRICE,
                                PRODUCT_1_STOCK_QUANTITY
                        )
                );

        assertThat(products.getSize()).isEqualTo(2);
        assertThat(products.getNumber()).isEqualTo(0);
        assertThat(products.getTotalElements()).isEqualTo(1);
        assertThat(products.getTotalPages()).isEqualTo(1);
    }

    private Product createProduct(String name, ProductType type, ProductSellingStatus sellingStatus, int price, int stockQuantity) {
        return Product.builder()
                .name(name)
                .productType(type)
                .productSellingStatus(sellingStatus)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}
