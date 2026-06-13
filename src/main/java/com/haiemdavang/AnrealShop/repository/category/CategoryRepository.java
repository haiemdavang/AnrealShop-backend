package com.haiemdavang.AnrealShop.repository.category;

import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByIdOrUrlPath(String id, String urlSlug);

    @Query(value = """
            WITH RECURSIVE category_tree AS (
                SELECT ma_danh_muc
                FROM   danh_muc
                WHERE  (ma_danh_muc = :param OR duong_dan_day_du = :param)
                  AND  da_xoa = false

                UNION ALL

                SELECT c.ma_danh_muc
                FROM   danh_muc c
                INNER JOIN category_tree ct ON c.ma_danh_muc_cha = ct.ma_danh_muc
                WHERE  c.da_xoa = false
            )
            SELECT ma_danh_muc FROM category_tree
            """, nativeQuery = true)
    List<String> findCategoryAndDescendantIds(@Param("param") String param);

    List<Category> findAllByIsDeletedFalseAndIsVisibleTrueOrderByCreatedAtAsc();

    List<Category> findAllByIsDeletedFalseAndIsVisibleFalseOrderByCreatedAtAsc();

    List<Category> findAllByIsDeletedFalse();

    Collection<Category> findAllByIdIn(Collection<String> ids);
}
