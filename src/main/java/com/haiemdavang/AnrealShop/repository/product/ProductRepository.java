package com.haiemdavang.AnrealShop.repository.product;

import com.haiemdavang.AnrealShop.dto.product.IProductStatus;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query(
            value = """
                select restrict_status as id, count(id) as count from products
                where shop_id = :shopId and deleted = false
                group by restrict_status""",
            nativeQuery = true
    )
    Set<IProductStatus> getMetaSumMyProductByStatus(String shopId);

    @EntityGraph(attributePaths = { "category", "shop",  })
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
            "category",
            "mediaList",
            "generalAttributes",
            "productSkus"
    })
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findWithCategoryAndMediaAndGeneralAttributeById(String id);

    @EntityGraph(attributePaths = {
            "category",
            "shop",
            "mediaList",
            "generalAttributes",
            "productSkus"
    })
    @Query("SELECT p FROM Product p WHERE p.id = :id or p.urlSlug = :id")
    Optional<Product> findFullInfoByIdOrSlug(String id);

    @Modifying
    @Query("UPDATE Product p SET p.deleted = true, p.visible = false WHERE p.id = :id")
    void softDelById(String id);
    @Modifying
    @Query("UPDATE Product p SET p.deleted = true, p.visible = false WHERE p.id in :ids")
    void softDelById(Collection<String> ids);

    Set<Product> findByIdIn(Collection<String> ids);

    void deleteByIdIn(Collection<String> ids);

    @EntityGraph(attributePaths = {
            "category",
            "shop",
            "mediaList",
    })
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Product findBaseInfoById(String id);

    @Query(
            value = """
                    SELECT
                                            status_list.id,
                                            COUNT(products.id) AS count
                                          FROM
                                            (
                                              SELECT 'ACTIVE' AS id
                                              UNION ALL
                                              SELECT 'PENDING'
                                              UNION ALL
                                              SELECT 'VIOLATION'
                                            ) AS status_list
                                          LEFT JOIN
                                            products ON (
                                              products.deleted = false AND (
                                                (status_list.id = 'ACTIVE'  AND products.restricted_reason IS NOT NULL AND products.restrict_status != 'VIOLATION') OR
                                                (status_list.id = 'PENDING' AND products.restrict_status = 'PENDING' AND products.restricted_reason IS NULL) OR
                                                (status_list.id = 'VIOLATION' AND products.restrict_status = 'VIOLATION')
                                              )
                                              and products.created_at >= :startDateTime AND products.created_at <= :enDateTime
                                            )
                                          GROUP BY
                                            status_list.id;""",
            nativeQuery = true
    )
    Set<IProductStatus> getMetaSumByStatusForAdmin(LocalDateTime startDateTime, LocalDateTime enDateTime);
}
