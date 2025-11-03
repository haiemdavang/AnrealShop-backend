package com.haiemdavang.AnrealShop.repository;

import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByIdOrUrlSlug(String id, String urlSlug);

    List<Category> findAllByIsDeletedFalseAndIsVisibleTrueOrderByCreatedAtAsc();

    List<Category> findAllByIsDeletedFalseAndIsVisibleFalseOrderByCreatedAtAsc();

    List<Category> findAllByParentIdAndIsDeletedFalse(String parentId);

    List<Category> findAllByIsDeletedFalse();
}
