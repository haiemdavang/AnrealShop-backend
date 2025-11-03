package com.haiemdavang.AnrealShop.repository.category;

import com.haiemdavang.AnrealShop.modal.entity.category.DisplayCategory;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayCategoryRepository extends JpaRepository<DisplayCategory, String> {


    @Query("SELECT dc FROM DisplayCategory dc JOIN FETCH dc.category order by dc.displayOrder ASC")
    List<DisplayCategory> findAllWithCategory();
    @EntityGraph(attributePaths = {"category"})
    List<DisplayCategory> findAllByPositionOrderByDisplayOrderAsc(CategoryDisplayPosition position);
}
