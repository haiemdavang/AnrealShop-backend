package com.haiemdavang.AnrealShop.repository.product;

import com.haiemdavang.AnrealShop.modal.entity.product.ProductReview;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, String> {
    @EntityGraph(attributePaths = {"user", "product", "mediaList"})
    Set<ProductReview> findByProductId(String productId);

    boolean existsByOrderItemId(String orderItemId);

    @EntityGraph(attributePaths = {"user", "product", "mediaList"})
    List<ProductReview> findByOrderItemIdIn(Collection<String> orderItemIds);

    @Query("SELECT pr.orderItem.id FROM ProductReview pr WHERE pr.orderItem.id IN :orderItemIds")
    Set<String> findReviewedOrderItemIds(Collection<String> orderItemIds);

    @EntityGraph(attributePaths = {"product", "mediaList"})
    List<ProductReview> findByUserIdOrderByCreatedAtDesc(String userId);
}
