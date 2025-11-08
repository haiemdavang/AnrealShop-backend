package com.haiemdavang.AnrealShop.repository.product;

import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductSkuRepository extends JpaRepository<ProductSku, String> {
    @Query("SELECT ps From ProductSku ps " +
            "LEFT JOIN FETCH ps.product p " +
            "LEFT JOIN FETCH p.shop s " +
            "WHERE ps.id in :productSkuIds")
    List<ProductSku> findByProductSkuIdIn(Collection<String> productSkuIds);

    @Query("SELECT ps FROM ProductSku ps LEFT JOIN FETCH ps.attributes a left join fetch a.attributeKey  WHERE ps.product.id = :id or ps.product.urlSlug = :id")
    List<ProductSku> findWithAttributeByProductIdOrProductSlug(String id);
}
