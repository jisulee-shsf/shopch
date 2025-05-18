package com.app.api.product.service;

import com.app.api.common.PageResponse;
import com.app.api.product.service.dto.request.ProductCreateServiceRequest;
import com.app.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.app.api.product.service.dto.response.ProductResponse;
import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import com.app.global.error.exception.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static com.app.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.domain.product.constant.ProductType.PRODUCT_B;
import static com.app.global.error.ErrorType.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
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

    @DisplayName("상품을 등록한다")
    @Test
    void createProduct() {
        // given
        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                .name("product")
                .productType(PRODUCT_A.name())
                .price(10000)
                .stockQuantity(1)
                .build();

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("product", PRODUCT_A.name(), SELLING.name(), 10000, 1);

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

        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
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
                .containsExactly("updatedProduct", PRODUCT_B.name(), SELLING.name(), 20000, 2);

        Optional<Product> optionalProduct = productRepository.findById(productId);
        assertThat(optionalProduct)
                .isPresent()
                .get()
                .extracting("name", "productType", "productSellingStatus", "price", "stockQuantity")
                .containsExactly("updatedProduct", PRODUCT_B, SELLING, 20000, 2);
    }

    @DisplayName("변경할 상품이 없을 때 변경을 시도할 경우, 예외가 발생한다.")
    @Test
    void updateProduct_ProductNotFound() {
        // given
        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(20000)
                .stockQuantity(2)
                .build();

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(1L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PRODUCT_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("판매 상태인 상품을 조회한다.")
    @Test
    void findSellingProducts() {
        // given
        Product product1 = createTestProduct("productA", SELLING, 1);
        Product product2 = createTestProduct("productB", SELLING, 2);
        Product product3 = createTestProduct("productC", COMPLETED, 0);
        productRepository.saveAll(List.of(product1, product2, product3));

        PageRequest pageRequest = PageRequest.of(0, 2);

        // when
        PageResponse<ProductResponse> pageResponse = productService.findSellingProducts(pageRequest);

        // then
        List<ProductResponse> content = pageResponse.getContent();
        assertThat(content).hasSize(2);
        assertThat(content)
                .extracting("name", "productSellingStatus", "stockQuantity")
                .containsExactly(
                        tuple("productA", SELLING.name(), 1),
                        tuple("productB", SELLING.name(), 2)
                );

        assertThat(pageResponse.getSize()).isEqualTo(2);
        assertThat(pageResponse.getNumber()).isEqualTo(0);
        assertThat(pageResponse.getTotalElements()).isEqualTo(2);
        assertThat(pageResponse.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("조회한 페이지에 컨텐츠가 없을 경우, 빈 페이지를 반환한다.")
    @Test
    void findSellingProducts_NoContent() {
        // given
        Product product1 = createTestProduct("productA", SELLING, 1);
        Product product2 = createTestProduct("productB", SELLING, 2);
        Product product3 = createTestProduct("productC", COMPLETED, 0);
        productRepository.saveAll(List.of(product1, product2, product3));

        PageRequest pageRequest = PageRequest.of(1, 2);

        // when
        PageResponse<ProductResponse> pageResponse = productService.findSellingProducts(pageRequest);

        // then
        assertThat(pageResponse.getContent()).isEmpty();
        assertThat(pageResponse.getSize()).isEqualTo(2);
        assertThat(pageResponse.getNumber()).isEqualTo(1);
        assertThat(pageResponse.getTotalElements()).isEqualTo(2);
        assertThat(pageResponse.getTotalPages()).isEqualTo(1);
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
