package com.app.api.product.service;

import com.app.api.product.dto.request.ProductCreateRequest;
import com.app.api.product.dto.request.ProductUpdateRequest;
import com.app.api.product.dto.response.ProductResponse;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import com.app.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.domain.product.constant.ProductType.PRODUCT_B;
import static com.app.global.error.ErrorType.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("product", PRODUCT_A, SELLING, 10000, 1);

        Optional<Product> optionalProduct = productRepository.findById(response.getId());
        assertThat(optionalProduct)
                .isPresent()
                .get()
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("product", PRODUCT_A, SELLING, 10000, 1);
    }

    @DisplayName("등록된 상품 정보를 주어진 상품 정보로 변경한다.")
    @Test
    void updateProduct() {
        // given
        Product product = Product.builder()
                .name("product")
                .productType(PRODUCT_A)
                .productSellingStatus(SELLING)
                .price(10000)
                .stockQuantity(1)
                .build();
        productRepository.save(product);

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updateProduct")
                .productType(PRODUCT_B)
                .price(20000)
                .stockQuantity(2)
                .build();

        Long productId = product.getId();

        // when
        ProductResponse response = productService.updateProduct(productId, request);

        // then
        assertThat(response.getId()).isEqualTo(productId);
        assertThat(response)
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("updateProduct", PRODUCT_B, SELLING, 20000, 2);

        Optional<Product> optionalProduct = productRepository.findById(productId);
        assertThat(optionalProduct)
                .isPresent()
                .get()
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("updateProduct", PRODUCT_B, SELLING, 20000, 2);
    }

    @DisplayName("변경할 상품이 없을 때 변경을 시도할 경우, 예외가 발생한다.")
    @Test
    void updateProduct_ProductDoesNotExist() {
        // given
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updateProduct")
                .productType(PRODUCT_B)
                .price(20000)
                .stockQuantity(2)
                .build();

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PRODUCT_NOT_FOUND.getErrorMessage());
    }
}
