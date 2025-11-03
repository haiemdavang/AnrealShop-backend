package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.category.AdminCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.BaseCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryModalSelectedDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryRequestDto;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    Category findByIdAndThrow(String categoryId);

    Category findByIdOrUrlSlug(String categoryId);

    Category findById(String categoryId);

    List<Category> getAllCategories();

    List<BaseCategoryDto> getCategorySuggest(String keyword);

    boolean existsById(String categoryId);

    Category getReferenceById(String categoryId);

    Set<BaseCategoryDto> getCategorySuggestByProductName(String keyword);

    List<CategoryModalSelectedDto> getCategoryMyShop();

    List<AdminCategoryDto> getListCategoryForAdmin();

    void addCategory(CategoryRequestDto categoryRequestDto);

    void updateCategory(String categoryId, CategoryRequestDto categoryRequestDto);

    void toggleIsVisible(String categoryId, boolean isToggleAll);

    void softDelete(String categoryId, boolean includeChildren);

    List<AdminCategoryDto> getListCategoryDisableForAdmin();
}
