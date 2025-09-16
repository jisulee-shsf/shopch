package com.shopch.api.product.service;

import com.shopch.api.common.dto.PageResponse;
import com.shopch.api.product.service.dto.request.ProductCreateServiceRequest;
import com.shopch.api.product.service.dto.request.ProductUpdateServiceRequest;
import com.shopch.api.product.service.dto.response.ProductResponse;
import com.shopch.domain.product.constant.ProductSellingStatus;
import com.shopch.domain.product.entity.Product;
import com.shopch.domain.product.repository.ProductRepository;
import com.shopch.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shopch.global.error.ErrorCode.PRODUCT_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductCreateServiceRequest request) {
        Product product = request.toProduct();
        return ProductResponse.of(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateServiceRequest request) {
        Product product = getProduct(productId);
        product.update(request);
        return ProductResponse.of(product);
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_NOT_FOUND));
    }

    public PageResponse<ProductResponse> findSellingProducts(Pageable pageable) {
        Page<Product> sellingProducts = productRepository.findAllByProductSellingStatusIn(ProductSellingStatus.forDisplay(), pageable);
        return PageResponse.of(sellingProducts.map(ProductResponse::of));
    }
}
