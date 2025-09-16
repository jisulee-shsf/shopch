package com.shopch.domain.product.repository;

import com.shopch.domain.product.constant.ProductSellingStatus;
import com.shopch.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllByProductSellingStatusIn(List<ProductSellingStatus> productSellingStatuses, Pageable pageable);
}
