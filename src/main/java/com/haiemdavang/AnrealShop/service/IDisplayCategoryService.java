package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayRequestDto;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;

import java.util.List;

public interface IDisplayCategoryService {

    List<CategoryDisplayDto> getListCategoriesDisplay(CategoryDisplayPosition categoryDisplayPosition);

    void updateCategoryDisplay(List<CategoryDisplayRequestDto> categoryDisplayRequestDtos);

    void deleteCategoriesDisplay(List<String> ids);
}
