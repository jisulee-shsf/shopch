package com.shopch.api.product.service;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.product.service.dto.request.ProductCreateServiceRequest;
import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.api.product.service.dto.response.ProductResponse;
import com.shopch.domain.product.constant.ProductSellingStatus;
import com.shopch.domain.product.constant.ProductType;
import com.shopch.domain.product.entity.Product;
import com.shopch.domain.product.repository.ProductRepository;
import com.shopch.global.error.exception.EntityNotFoundException;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.shopch.domain.product.constant.ProductSellingStatus.COMPLETED;
import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_2;
import static com.shopch.global.error.ErrorCode.PRODUCT_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ProductServiceTest extends IntegrationTestSupport {

    private static final String PRODUCT_1_NAME = "product1";
    private static final Integer PRODUCT_1_PRICE = 10000;
    private static final Integer PRODUCT_1_STOCK_QUANTITY = 10;
    private static final int EXPECTED_SIZE_1 = 1;
    private static final String PRODUCT_2_NAME = "product2";
    private static final Integer PRODUCT_2_PRICE = 20000;
    private static final Integer PRODUCT_2_STOCK_QUANTITY = 20;
    private static final Long NON_EXISTENT_PRODUCT_ID = 1L;
    private static final int ZERO_STOCK_QUANTITY = 0;
    private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 2);
    private static final int EXPECTED_SIZE_2 = 2;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void tearDown() {
        productRepository.deleteAllInBatch();
    }

    @DisplayName("상품을 생성한다.")
    @Test
    void createProduct() {
        // given
        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1)
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting(
                        ProductResponse::getName,
                        ProductResponse::getProductType,
                        ProductResponse::getProductSellingStatus,
                        ProductResponse::getPrice,
                        ProductResponse::getStockQuantity
                )
                .containsExactly(
                        PRODUCT_1_NAME,
                        PRODUCT_1.name(),
                        SELLING.name(),
                        PRODUCT_1_PRICE,
                        PRODUCT_1_STOCK_QUANTITY
                );

        assertThat(productRepository.findAll()).hasSize(EXPECTED_SIZE_1)
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
    }

    @DisplayName("등록된 상품 정보를 주어진 상품 정보로 변경한다.")
    @Test
    void updateProduct() {
        // given
        Product product = createProduct(PRODUCT_1_NAME, PRODUCT_1, PRODUCT_1_PRICE, PRODUCT_1_STOCK_QUANTITY);
        productRepository.save(product);

        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2)
                .price(PRODUCT_2_PRICE)
                .stockQuantity(PRODUCT_2_STOCK_QUANTITY)
                .build();

        Long productId = product.getId();

        // when
        ProductResponse response = productService.updateProduct(productId, request);

        // then
        assertThat(response)
                .extracting(
                        ProductResponse::getId,
                        ProductResponse::getName,
                        ProductResponse::getProductType,
                        ProductResponse::getProductSellingStatus,
                        ProductResponse::getPrice,
                        ProductResponse::getStockQuantity
                )
                .containsExactly(
                        productId,
                        PRODUCT_2_NAME,
                        PRODUCT_2.name(),
                        SELLING.name(),
                        PRODUCT_2_PRICE,
                        PRODUCT_2_STOCK_QUANTITY
                );

        assertThat(productRepository.findAll()).hasSize(EXPECTED_SIZE_1)
                .extracting(
                        Product::getName,
                        Product::getProductType,
                        Product::getProductSellingStatus,
                        Product::getPrice,
                        Product::getStockQuantity
                )
                .containsExactly(
                        tuple(
                                PRODUCT_2_NAME,
                                PRODUCT_2,
                                SELLING,
                                PRODUCT_2_PRICE,
                                PRODUCT_2_STOCK_QUANTITY
                        )
                );
    }

    @DisplayName("등록된 상품이 없을 때 정보 변경을 시도할 경우, 예외가 발생한다.")
    @Test
    void updateProduct_ProductNotFound() {
        // given
        ProductUpdateServiceRequest request = ProductUpdateServiceRequest.builder()
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2)
                .price(PRODUCT_2_PRICE)
                .stockQuantity(PRODUCT_2_STOCK_QUANTITY)
                .build();

        // when & then
        assertThatThrownBy(() -> productService.updateProduct(NON_EXISTENT_PRODUCT_ID, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(PRODUCT_NOT_FOUND.getMessage());
    }

    @DisplayName("등록된 판매 상품과 페이징 정보를 조회한다.")
    @Test
    void findSellingProducts() {
        // given
        Product product1 = createProduct(PRODUCT_1_NAME, PRODUCT_1, SELLING, PRODUCT_1_PRICE, PRODUCT_1_STOCK_QUANTITY);
        Product product2 = createProduct(PRODUCT_2_NAME, PRODUCT_2, SELLING, PRODUCT_2_PRICE, PRODUCT_2_STOCK_QUANTITY);
        Product product3 = createProduct(PRODUCT_1_NAME, PRODUCT_1, COMPLETED, PRODUCT_1_PRICE, ZERO_STOCK_QUANTITY);
        productRepository.saveAll(List.of(product1, product2, product3));

        // when
        PageResponse<ProductResponse> response = productService.findSellingProducts(DEFAULT_PAGE_REQUEST);

        // then
        assertThat(response.getContent()).hasSize(EXPECTED_SIZE_2)
                .extracting(
                        ProductResponse::getId,
                        ProductResponse::getName,
                        ProductResponse::getProductType,
                        ProductResponse::getProductSellingStatus,
                        ProductResponse::getPrice,
                        ProductResponse::getStockQuantity
                )
                .containsExactly(
                        tuple(
                                product1.getId(),
                                PRODUCT_1_NAME,
                                PRODUCT_1.name(),
                                SELLING.name(),
                                PRODUCT_1_PRICE,
                                PRODUCT_1_STOCK_QUANTITY
                        ),
                        tuple(
                                product2.getId(),
                                PRODUCT_2_NAME,
                                PRODUCT_2.name(),
                                SELLING.name(),
                                PRODUCT_2_PRICE,
                                PRODUCT_2_STOCK_QUANTITY
                        )
                );

        assertThat(response)
                .extracting(
                        PageResponse::getSize,
                        PageResponse::getNumber,
                        PageResponse::getTotalElements,
                        PageResponse::getTotalPages
                )
                .containsExactly(
                        2, 0, 2L, 1
                );

        assertThat(response.getSize()).isEqualTo(2);
        assertThat(response.getNumber()).isEqualTo(0);
        assertThat(response.getTotalElements()).isEqualTo(2L);
        assertThat(response.getTotalPages()).isEqualTo(1);
    }

    @DisplayName("등록된 판매 상품이 없을 경우, 빈 페이지를 반환한다.")
    @Test
    void findSellingProducts_NoContent() {
        // when
        PageResponse<ProductResponse> response = productService.findSellingProducts(DEFAULT_PAGE_REQUEST);

        // then
        assertThat(response.getContent()).isEmpty();
        assertThat(response)
                .extracting(
                        PageResponse::getSize,
                        PageResponse::getNumber,
                        PageResponse::getTotalElements,
                        PageResponse::getTotalPages
                )
                .containsExactly(
                        2, 0, 0L, 0
                );
    }

    private Product createProduct(String name, ProductType type, int price, int stockQuantity) {
        return createProduct(name, type, SELLING, price, stockQuantity);
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
