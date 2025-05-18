package com.app.api.product.controller;

import com.app.api.common.PageResponse;
import com.app.api.product.dto.request.ProductCreateRequest;
import com.app.api.product.dto.request.ProductUpdateRequest;
import com.app.api.product.service.ProductService;
import com.app.api.product.service.dto.response.ProductResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(productService.createProduct(request.toServiceRequest()));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId,
                                                         @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request.toServiceRequest()));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> findSellingProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findSellingProducts(pageable));
    }
}
