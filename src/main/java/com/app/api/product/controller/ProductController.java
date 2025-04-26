package com.app.api.product.controller;

import com.app.api.common.PageResponse;
import com.app.api.product.dto.request.ProductCreateRequest;
import com.app.api.product.dto.request.ProductUpdateRequest;
import com.app.api.product.dto.response.ProductResponse;
import com.app.api.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId,
                                                         @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @GetMapping("/products")
    public ResponseEntity<PageResponse<ProductResponse>> findSellingProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findSellingProducts(pageable));
    }
}
