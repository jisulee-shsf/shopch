package com.app.api.product.service;

import com.app.api.common.PageResponse;
import com.app.api.product.service.dto.request.ProductCreateServiceRequest;
import com.app.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.app.api.product.service.dto.response.ProductResponse;
import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.entity.Product;
import com.app.domain.product.repository.ProductRepository;
import com.app.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.app.global.error.ErrorType.PRODUCT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        Product product = request.toEntity();
        Product savedProduct = productRepository.save(product);
        return ProductResponse.of(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateServiceRequest request) {
        Product product = findProductById(productId);
        product.update(request);
        return ProductResponse.of(product);
    }

    public Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND));
    }

    public PageResponse<ProductResponse> findSellingProducts(Pageable pageable) {
        Page<Product> pageProducts = productRepository.findAllByProductSellingStatusIn(ProductSellingStatus.forDisplay(), pageable);
        return PageResponse.of(pageProducts.map(ProductResponse::of));
    }
}
