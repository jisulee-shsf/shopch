package com.app.domain.product.repository;

import com.app.domain.product.constant.ProductSellingStatus;
import com.app.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByProductSellingStatusIn(List<ProductSellingStatus> productSellingStatuses, Pageable pageable);
}
