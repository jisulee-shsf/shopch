package com.shopch.api.product.controller;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.product.controller.dto.ProductCreateRequest;
import com.shopch.api.product.controller.dto.ProductUpdateRequest;
import com.shopch.api.product.service.dto.request.ProductCreateServiceRequest;
import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.api.product.service.dto.response.ProductResponse;
import com.shopch.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_2;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends ControllerTestSupport {

    private static final String PRODUCT_1_NAME = "product1";
    private static final Integer PRODUCT_1_PRICE = 10000;
    private static final Integer PRODUCT_1_STOCK_QUANTITY = 10;
    private static final Long PRODUCT_1_ID = 1L;
    private static final String BAD_REQUEST_CODE = String.valueOf(HttpStatus.BAD_REQUEST.value());
    private static final String INVALID_PRODUCT_TYPE = "INVALID";
    private static final String PRODUCT_2_NAME = "product2";
    private static final Integer PRODUCT_2_PRICE = 20000;
    private static final Integer PRODUCT_2_STOCK_QUANTITY = 20;
    private static final Long PRODUCT_2_ID = 2L;
    private static Stream<String> blankStringProvider() {
        return Stream.of("", " ");
    }

    @DisplayName("상품 등록 요청을 처리한 후, 등록된 상품 정보를 반환한다.")
    @Test
    void createProduct() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1.name())
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(PRODUCT_1_ID)
                .name(request.getName())
                .productType(request.getProductType())
                .productSellingStatus(SELLING.name())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        given(productService.createProduct(any(ProductCreateServiceRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("상품 등록 시 상품 이름은 필수이다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("blankStringProvider")
    void createProduct_MissingName(String input) throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(input)
                .productType(PRODUCT_1.name())
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value("[name] 상품 이름은 필수입니다."));
    }

    @DisplayName("상품 등록 시 상품 타입은 필수이다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("blankStringProvider")
    void createProduct_MissingProductType(String input) throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(PRODUCT_1_NAME)
                .productType(input)
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value("[productType] 상품 타입은 필수입니다."));
    }

    @DisplayName("상품 등록 시 유효한 상품 타입은 필수이다.")
    @Test
    void createProduct_InvalidProductType() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(PRODUCT_1_NAME)
                .productType(INVALID_PRODUCT_TYPE)
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value("[productType] 유효하지 않은 상품 타입입니다."));
    }

    private static Stream<Arguments> invalidPriceProvider() {
        return Stream.of(
                Arguments.of(null, "[price] 상품 가격은 필수입니다."),
                Arguments.of(-1, "[price] 상품 가격은 양수여야 합니다."),
                Arguments.of(0, "[price] 상품 가격은 양수여야 합니다.")
        );
    }

    @DisplayName("상품 등록 시 상품 가격은 필수이며, 양수여야 한다.")
    @ParameterizedTest
    @MethodSource("invalidPriceProvider")
    void createProduct_InvalidPrice(Integer input, String expectedMessage) throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1.name())
                .price(input)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    private static Stream<Arguments> invalidCreateStockQuantityProvider() {
        return Stream.of(
                Arguments.of(null, "[stockQuantity] 상품 재고 수량은 필수입니다."),
                Arguments.of(-1, "[stockQuantity] 상품 등록 시 재고 수량은 양수여야 합니다."),
                Arguments.of(0, "[stockQuantity] 상품 등록 시 재고 수량은 양수여야 합니다.")
        );
    }

    @DisplayName("상품 등록 시 상품 재고 수량은 필수이며, 양수여야 한다.")
    @ParameterizedTest
    @MethodSource("invalidCreateStockQuantityProvider")
    void createProduct_InvalidStockQuantity(Integer input, String expectedMessage) throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1.name())
                .price(PRODUCT_1_PRICE)
                .stockQuantity(input)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @DisplayName("상품 수정 요청을 처리한 후, 변경된 상품 정보를 반환한다.")
    @Test
    void updateProduct() throws Exception {
        // given
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2.name())
                .price(PRODUCT_2_PRICE)
                .stockQuantity(PRODUCT_2_STOCK_QUANTITY)
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(PRODUCT_1_ID)
                .name(request.getName())
                .productType(request.getProductType())
                .productSellingStatus(SELLING.name())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        given(productService.updateProduct(eq(PRODUCT_1_ID), any(ProductUpdateServiceRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/products/{productId}", PRODUCT_1_ID)
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> invalidUpdateStockQuantityProvider() {
        return Stream.of(
                Arguments.of(null, "[stockQuantity] 상품 재고 수량은 필수입니다."),
                Arguments.of(-1, "[stockQuantity] 상품 수정 시 재고 수량은 0 이상이어야 합니다.")
        );
    }

    @DisplayName("상품 수정 시 상품 재고 수량은 필수이며, 0 이상이어야 한다.")
    @ParameterizedTest
    @MethodSource("invalidUpdateStockQuantityProvider")
    void updateProduct_InvalidStockQuantity(Integer input, String expectedMessage) throws Exception {
        // given
        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2.name())
                .price(PRODUCT_2_PRICE)
                .stockQuantity(input)
                .build();

        // when & then
        mockMvc.perform(put("/api/products/{productId}", PRODUCT_1_ID)
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @DisplayName("등록된 판매 상품과 페이징 정보를 조회해 반환한다.")
    @Test
    void findSellingProducts() throws Exception {
        // given

        ProductResponse response1 = ProductResponse.builder()
                .id(PRODUCT_1_ID)
                .name(PRODUCT_1_NAME)
                .productType(PRODUCT_1.name())
                .productSellingStatus(SELLING.name())
                .price(PRODUCT_1_PRICE)
                .stockQuantity(PRODUCT_1_STOCK_QUANTITY)
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(PRODUCT_2_ID)
                .name(PRODUCT_2_NAME)
                .productType(PRODUCT_2.name())
                .productSellingStatus(SELLING.name())
                .price(PRODUCT_2_PRICE)
                .stockQuantity(PRODUCT_2_STOCK_QUANTITY)
                .build();

        Pageable pageable = PageRequest.of(0, 2);

        given(productService.findSellingProducts(any(Pageable.class)))
                .willReturn(PageResponse.of(new PageImpl<>(List.of(response1, response2), pageable, 2)));

        // when & then
        mockMvc.perform(get("/api/products/selling")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                )
                .andExpect(status().isOk());
    }
}
