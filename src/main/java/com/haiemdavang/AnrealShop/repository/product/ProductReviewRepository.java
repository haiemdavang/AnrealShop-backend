package com.haiemdavang.AnrealShop.repository.product;

import com.haiemdavang.AnrealShop.modal.entity.product.ProductReview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {
    @EntityGraph(attributePaths = {"user", "product", "mediaList"})
    Set<ProductReview> findByProductId(String productId);

    boolean existsByOrderItemId(String orderItemId);
}
