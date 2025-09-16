package com.shopch.docs.product;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.product.controller.ProductController;
import com.shopch.api.product.controller.dto.ProductCreateRequest;
import com.shopch.api.product.controller.dto.ProductUpdateRequest;
import com.shopch.api.product.service.ProductService;
import com.shopch.api.product.service.dto.request.ProductCreateServiceRequest;
import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.api.product.service.dto.response.ProductResponse;
import com.shopch.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.shopch.domain.product.constant.ProductSellingStatus.SELLING;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_1;
import static com.shopch.domain.product.constant.ProductType.PRODUCT_2;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerDocsTest extends RestDocsSupport {

    private static final String PRODUCT_1_NAME = "product1";
    private static final Integer PRODUCT_1_PRICE = 10000;
    private static final Integer PRODUCT_1_STOCK_QUANTITY = 10;
    private static final Long PRODUCT_1_ID = 1L;
    private static final String PRODUCT_2_NAME = "product2";
    private static final Integer PRODUCT_2_PRICE = 20000;
    private static final Integer PRODUCT_2_STOCK_QUANTITY = 20;
    private static final Long PRODUCT_2_ID = 2L;

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("상품 등록")
    @Test
    void createProduct() throws Exception {
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

        mockMvc.perform(post("/api/products")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("product-create",
                        requestFields(
                                fieldWithPath("name").type(STRING).description("상품 이름"),
                                fieldWithPath("productType").type(STRING).description("상품 타입"),
                                fieldWithPath("price").type(NUMBER).description("상품 금액"),
                                fieldWithPath("stockQuantity").type(NUMBER).description("상품 재고 수량")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("name").type(STRING).description("상품 이름"),
                                fieldWithPath("productType").type(STRING).description("상품 타입"),
                                fieldWithPath("productSellingStatus").type(STRING).description("상품 판매 상태"),
                                fieldWithPath("price").type(NUMBER).description("상품 금액"),
                                fieldWithPath("stockQuantity").type(NUMBER).description("상품 재고 수량")
                        )
                ));
    }

    @DisplayName("상품 수정")
    @Test
    void updateProduct() throws Exception {
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

        mockMvc.perform(put("/api/products/{productId}", PRODUCT_1_ID)
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("product-update",
                        requestFields(
                                fieldWithPath("name").type(STRING).description("상품 이름"),
                                fieldWithPath("productType").type(STRING).description("상품 타입"),
                                fieldWithPath("price").type(NUMBER).description("상품 금액"),
                                fieldWithPath("stockQuantity").type(NUMBER).description("상품 재고 수량")
                        ),
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("name").type(STRING).description("상품 이름"),
                                fieldWithPath("productType").type(STRING).description("상품 타입"),
                                fieldWithPath("productSellingStatus").type(STRING).description("상품 판매 상태"),
                                fieldWithPath("price").type(NUMBER).description("상품 금액"),
                                fieldWithPath("stockQuantity").type(NUMBER).description("상품 재고 수량")
                        )
                ));
    }

    @DisplayName("판매 상품 및 페이징 정보 조회")
    @Test
    void findSellingProducts() throws Exception {
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

        mockMvc.perform(get("/api/products/selling")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                )
                .andExpect(status().isOk())
                .andDo(document("product-find",
                        responseFields(
                                fieldWithPath("content.[]").type(ARRAY).description("판매 상품 목록"),
                                fieldWithPath("content.[].id").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("content.[].name").type(STRING).description("상품 이름"),
                                fieldWithPath("content.[].productType").type(STRING).description("상품 타입"),
                                fieldWithPath("content.[].productSellingStatus").type(STRING).description("상품 판매 상태"),
                                fieldWithPath("content.[].price").type(NUMBER).description("상품 금액"),
                                fieldWithPath("content.[].stockQuantity").type(NUMBER).description("상품 재고 수량"),
                                fieldWithPath("size").type(NUMBER).description("페이지 사이즈"),
                                fieldWithPath("number").type(NUMBER).description("페이지 번호"),
                                fieldWithPath("totalElements").type(NUMBER).description("전체 상품 수"),
                                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수")
                        )
                ));
    }
}
