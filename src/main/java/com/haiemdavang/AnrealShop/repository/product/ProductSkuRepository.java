package com.haiemdavang.AnrealShop.repository.product;

import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Query("UPDATE ProductSku s SET s.quantity = s.quantity - :quantity WHERE s.id = :skuId AND s.quantity >= :quantity")
    int deductStock(@Param("skuId") String skuId, @Param("quantity") int quantity);

    @Query("SELECT s.product.id FROM ProductSku s WHERE s.id = :skuId")
    String findProductIdBySkuId(@Param("skuId") String skuId);
}
