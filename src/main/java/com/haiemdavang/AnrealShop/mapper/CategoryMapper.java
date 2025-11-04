package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.category.AdminCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.BaseCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryModalSelectedDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryRequestDto;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryMapper {
    public CategoryModalSelectedDto toCategoryModalSelectedDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryModalSelectedDto.builder()
                .id(category.getId())
                .name(category.getName())
                .urlPath(category.getUrlPath())
                .urlSlug(category.getUrlSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .hasChildren(category.isHasChildren())
                .level(category.getLevel())
                .build();
    }

    public BaseCategoryDto toBaseCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return BaseCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .urlPath(category.getUrlPath())
                .urlSlug(category.getUrlSlug())
                .build();
    }
    public BaseCategoryDto toBaseCategoryDto(EsCategory category) {
        if (category == null) {
            return null;
        }

        return BaseCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .urlPath(category.getUrlPath())
                .urlSlug(category.getUrlSlug())
                .build();
    }


    public AdminCategoryDto toAdminCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return AdminCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getUrlSlug())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .description(category.getDescription())
                .level(category.getLevel())
                .hasChildren(category.isHasChildren())
                .productCount(category.getProductCount())
                .isVisible(category.isVisible())
                .build();
    }

    public List<AdminCategoryDto> toAdminCategoryDtoList(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }

        return categories.stream()
                .map(this::toAdminCategoryDto)
                .toList();
    }


    public Category toCategoryEntity(CategoryRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Category.builder()
                .name(dto.getName())
                .urlSlug(dto.getSlug())
                .description(dto.getDescription())
                .level(dto.getLevel())
                .isVisible(dto.isVisible())
                .build();
    }

    public void updateCategoryFromDto(CategoryRequestDto categoryRequestDto, Category category) {
        if (categoryRequestDto == null || category == null) {
            return;
        }

        if (categoryRequestDto.getName() != null) {
            category.setName(categoryRequestDto.getName());
        }
        if (categoryRequestDto.getSlug() != null) {
            category.setUrlSlug(categoryRequestDto.getSlug());
        }
        if (categoryRequestDto.getDescription() != null) {
            category.setDescription(categoryRequestDto.getDescription());
        }
        category.setLevel(categoryRequestDto.getLevel());
    }
}
