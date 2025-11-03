package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayRequestDto;
import com.haiemdavang.AnrealShop.modal.entity.category.DisplayCategory;
import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisplayCategoryMapper {

    public CategoryDisplayDto toCategoryDisplayDto(DisplayCategory displayCategory) {
        if (displayCategory == null) {
            return null;
        }

        return CategoryDisplayDto.builder()
                .id(displayCategory.getId())
                .categoryId(displayCategory.getCategory().getId())
                .categoryName(displayCategory.getCategory().getName())
                .position(displayCategory.getPosition())
                .order(displayCategory.getDisplayOrder())
                .thumbnailUrl(displayCategory.getThumbnailUrl())
                .mediaType(displayCategory.getMediaType())
                .build();
    }

    public List<CategoryDisplayDto> toCategoryDisplayDtos(List<DisplayCategory> categoryDisplayDtos) {
        return categoryDisplayDtos.stream()
                .map(this::toCategoryDisplayDto)
                .toList();
    }

    public void updateDisplayCategoryFields(DisplayCategory displayCategory, CategoryDisplayRequestDto dto) {
        displayCategory.setPosition(dto.getPosition());
        displayCategory.setDisplayOrder(dto.getOrder());
        displayCategory.setThumbnailUrl(dto.getThumbnailUrl() != null ? dto.getThumbnailUrl() : ApplicationInitHelper.IMAGE_USER_DEFAULT);
        displayCategory.setMediaType(dto.getMediaType() != null ? dto.getMediaType() : MediaType.IMAGE);

    }
}

