package com.haiemdavang.AnrealShop.repository.category;

import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByIdOrUrlSlug(String id, String urlSlug);

    List<Category> findAllByIsDeletedFalseAndIsVisibleTrueOrderByCreatedAtAsc();

    List<Category> findAllByIsDeletedFalseAndIsVisibleFalseOrderByCreatedAtAsc();

    List<Category> findAllByIsDeletedFalse();

    Collection<Category> findAllByIdIn(Collection<String> ids);


    @Query("""
        SELECT C FROM Category C
            WHERE (:keyword IS NULL OR :keyword = ''
                OR lower(C.urlPath) LIKE lower(concat('%', :keyword, '%') )
                OR lower(C.urlSlug) LIKE lower(concat('%', :keyword, '%') )
            ) and c.isDeleted = false and c.isVisible = true order by c.createdAt ASC
    """)
    List<Category> getSuggestByKeyWord(String keyword);

    @Query("""
        select distinct c
            from Product p
            join p.category c
            where p.shop.id like :keyword
            and p.deleted = false
            and c.isVisible = true
            and c.isDeleted = false
            AND (
                          :keyword IS NULL OR :keyword = ''
                          OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
    """)
    List<Category> getSuggestByProductName(String keyword);
}
