package com.app.docs.product;

import com.app.api.common.PageResponse;
import com.app.api.product.controller.ProductController;
import com.app.api.product.controller.dto.request.ProductCreateRequest;
import com.app.api.product.controller.dto.request.ProductUpdateRequest;
import com.app.api.product.service.ProductService;
import com.app.api.product.service.dto.request.ProductCreateServiceRequest;
import com.app.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.app.api.product.service.dto.response.ProductResponse;
import com.app.support.RestDocsSupport;
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

    private final ProductService productService = mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @DisplayName("상품 등록")
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

    @DisplayName("판매 상품 조회")
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
                                fieldWithPath("size").type(NUMBER).description("한 페이지에 포함된 상품 수"),
                                fieldWithPath("number").type(NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("totalElements").type(NUMBER).description("전체 상품 수"),
                                fieldWithPath("totalPages").type(NUMBER).description("전체 페이지 수")
                        )
                ));
    }
}
