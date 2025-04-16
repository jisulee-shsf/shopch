package com.app.api.product.service;

import com.app.api.product.dto.request.ProductCreateRequest;
import com.app.api.product.dto.response.ProductResponse;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("신규 상품을 등록한다")
    @Test
    void createProduct() {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType(PRODUCT_A)
                .price(10000)
                .stockQuantity(1)
                .build();

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("name", "productType", "price", "stockQuantity")
                .containsExactly("product", PRODUCT_A, 10000, 1);

        Optional<Product> optionalProduct = productRepository.findById(response.getId());
        assertThat(optionalProduct)
                .isPresent()
                .get()
                .extracting("name", "productType", "price", "stockQuantity")
                .containsExactly("product", PRODUCT_A, 10000, 1);
    }
}
