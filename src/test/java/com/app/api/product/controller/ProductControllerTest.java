package com.app.api.product.controller;

import com.app.api.common.PageResponse;
import com.app.api.product.controller.dto.request.ProductCreateRequest;
import com.app.api.product.controller.dto.request.ProductUpdateRequest;
import com.app.api.product.service.dto.request.ProductCreateServiceRequest;
import com.app.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.app.api.product.service.dto.response.ProductResponse;
import com.app.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.app.domain.product.constant.ProductSellingStatus.SELLING;
import static com.app.domain.product.constant.ProductType.PRODUCT_A;
import static com.app.domain.product.constant.ProductType.PRODUCT_B;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerTest extends ControllerTestSupport {

    @DisplayName("상품을 등록한다.")
    @Test
    void createProduct() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType(PRODUCT_A.name())
                .price(10000)
                .stockQuantity(1)
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
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
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("상품 등록 시 상품 이름은 필수이다.")
    @Test
    void createProduct_MissingName() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("")
                .productType(PRODUCT_A.name())
                .price(10000)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[name] 상품 이름은 필수입니다."));
    }

    @DisplayName("상품 등록 시 상품 타입은 필수이다.")
    @Test
    void createProduct_MissingProductType() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType("")
                .price(10000)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[productType] 상품 타입은 필수입니다."));
    }

    @DisplayName("상품 등록 시 유효한 상품 타입은 필수이다.")
    @Test
    void createProduct_InvalidProductType() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType("invalid-product-type")
                .price(10000)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[productType] 유효한 상품 타입이 아닙니다."));
    }

    @DisplayName("상품 등록 시 상품 가격은 필수이다.")
    @Test
    void createProduct_MissingPrice() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType(PRODUCT_A.name())
                .price(null)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[price] 등록 상품 가격은 필수입니다."));
    }

    @DisplayName("상품 등록 시 상품 가격은 양수여야 한다.")
    @Test
    void createProduct_ZeroPrice() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType(PRODUCT_A.name())
                .price(0)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[price] 등록 상품 가격은 양수여야 합니다."));
    }

    @DisplayName("상품 등록 시 상품 재고 수량은 필수이다.")
    @Test
    void createProduct_MissingStockQuantity() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType(PRODUCT_A.name())
                .price(10000)
                .stockQuantity(null)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[stockQuantity] 등록 상품 재고 수량은 필수입니다."));
    }

    @DisplayName("상품 등록 시 상품 재고 수량은 양수여야 한다.")
    @Test
    void createProduct_ZeroStockQuantity() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("product")
                .productType(PRODUCT_A.name())
                .price(10000)
                .stockQuantity(0)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[stockQuantity] 등록 상품 재고 수량은 양수여야 합니다."));
    }

    @DisplayName("등록된 상품 정보를 주어진 상품 정보로 변경한다.")
    @Test
    void updateProduct() throws Exception {
        // given
        Long productId = 1L;

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(10000)
                .stockQuantity(1)
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(productId)
                .name(request.getName())
                .productType(request.getProductType())
                .productSellingStatus(SELLING.name())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        given(productService.updateProduct(eq(productId), any(ProductUpdateServiceRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("상품 정보 수정 시 상품 가격은 필수이다.")
    @Test
    void updateProduct_MissingPrice() throws Exception {
        // given
        Long productId = 1L;

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(null)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[price] 수정 상품 가격은 필수입니다."));
    }

    @DisplayName("상품 정보 수정 시 상품 가격은 양수여야 한다.")
    @Test
    void updateProduct_ZeroPrice() throws Exception {
        // given
        Long productId = 1L;

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(0)
                .stockQuantity(1)
                .build();

        // when & then
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[price] 수정 상품 가격은 양수여야 합니다."));
    }

    @DisplayName("상품 정보 수정 시 상품 재고 수량은 필수이다.")
    @Test
    void updateProduct_MissingStockQuantity() throws Exception {
        // given
        Long productId = 1L;

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(10000)
                .stockQuantity(null)
                .build();

        // when & then
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[stockQuantity] 수정 상품 재고 수량은 필수입니다."));
    }

    @DisplayName("상품 정보 수정 시 상품 재고 수량은 0 이상이여야 한다.")
    @Test
    void updateProduct_NegativeStockQuantity() throws Exception {
        // given
        Long productId = 1L;

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("updatedProduct")
                .productType(PRODUCT_B.name())
                .price(10000)
                .stockQuantity(-1)
                .build();

        // when & then
        mockMvc.perform(put("/api/products/{productId}", productId)
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[stockQuantity] 수정 상품 재고 수량은 0 이상이어야 합니다."));
    }

    @DisplayName("판매 상태인 상품과 페이징 결과를 조회한다.")
    @Test
    void findSellingProducts() throws Exception {
        // given
        PageRequest pageRequest = PageRequest.of(0, 2);

        ProductResponse response1 = ProductResponse.builder()
                .id(1L)
                .name("productA")
                .productType(PRODUCT_A.name())
                .productSellingStatus(SELLING.name())
                .price(10000)
                .stockQuantity(1)
                .build();

        ProductResponse response2 = ProductResponse.builder()
                .id(2L)
                .name("productB")
                .productType(PRODUCT_B.name())
                .productSellingStatus(SELLING.name())
                .price(20000)
                .stockQuantity(2)
                .build();

        Page<ProductResponse> pageResponse = new PageImpl<>(List.of(response1, response2), pageRequest, 2);
        given(productService.findSellingProducts(any(Pageable.class)))
                .willReturn(PageResponse.of(pageResponse));

        // when & then
        mockMvc.perform(get("/api/products")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .param("page", String.valueOf(pageRequest.getPageNumber()))
                        .param("size", String.valueOf(pageRequest.getPageSize()))
                )
                .andExpect(status().isOk());
    }
}
