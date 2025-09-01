package com.shopch.api.product.controller;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.product.controller.dto.ProductCreateRequest;
import com.shopch.api.product.controller.dto.ProductUpdateRequest;
import com.shopch.api.product.service.ProductService;
import com.shopch.api.product.service.dto.response.ProductResponse;
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

    @GetMapping("/selling")
    public ResponseEntity<PageResponse<ProductResponse>> findSellingProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.findSellingProducts(pageable));
    }
}
