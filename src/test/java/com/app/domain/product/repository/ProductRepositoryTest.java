package com.app.domain.product.repository;

import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static com.app.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("판매 상태가 SELLING인 상품과 페이징 결과를 조회한다.")
    @Test
    void findAllSellingStatusIn() {
        // given
        Product product1 = createTestProduct("productA", SELLING, 1);
        Product product2 = createTestProduct("productB", SELLING, 2);
        Product product3 = createTestProduct("productC", COMPLETED, 0);
        productRepository.saveAll(List.of(product1, product2, product3));

        PageRequest pageRequest = PageRequest.of(0, 2);

        // when
        Page<Product> pageProduct = productRepository.findAllByProductSellingStatusIn(List.of(SELLING), pageRequest);

        // then
        List<Product> content = pageProduct.getContent();
        assertThat(content).hasSize(2);
        assertThat(content)
                .extracting("name", "productSellingStatus", "stockQuantity")
                .containsExactly(
                        tuple("productA", SELLING, 1),
                        tuple("productB", SELLING, 2)
                );

        assertThat(pageProduct.getSize()).isEqualTo(2);
        assertThat(pageProduct.getNumber()).isEqualTo(0);
        assertThat(pageProduct.getTotalElements()).isEqualTo(2);
        assertThat(pageProduct.getTotalPages()).isEqualTo(1);
    }

    private Product createTestProduct(String name, ProductSellingStatus productSellingStatus, int stockQuantity) {
        return Product.builder()
                .name(name)
                .productType(PRODUCT_A)
                .productSellingStatus(productSellingStatus)
                .price(10000)
                .stockQuantity(stockQuantity)
                .build();
    }
}
