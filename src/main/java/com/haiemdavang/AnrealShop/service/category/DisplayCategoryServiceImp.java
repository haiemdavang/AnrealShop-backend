package com.haiemdavang.AnrealShop.service.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayRequestDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.DisplayCategoryMapper;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import com.haiemdavang.AnrealShop.modal.entity.category.DisplayCategory;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;
import com.haiemdavang.AnrealShop.repository.category.CategoryRepository;
import com.haiemdavang.AnrealShop.repository.category.DisplayCategoryRepository;
import com.haiemdavang.AnrealShop.service.IDisplayCategoryService;
import com.haiemdavang.AnrealShop.tech.redis.config.RedisTemplate;
import com.haiemdavang.AnrealShop.tech.redis.service.IRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisplayCategoryServiceImp implements IDisplayCategoryService {
    private final DisplayCategoryRepository categoryDisplayRepository;
    private final CategoryRepository categoryRepository;
    private final DisplayCategoryMapper displayCategoryMapper;
    private final IRedisService redisService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public List<CategoryDisplayDto> getListCategoriesDisplay(CategoryDisplayPosition categoryDisplayPosition) {
        List<DisplayCategory> categoryDisplayDtos = null;
        if (categoryDisplayPosition == null) {
            categoryDisplayDtos = categoryDisplayRepository.findAllWithCategory();
        } else {
            if (categoryDisplayPosition == CategoryDisplayPosition.HOMEPAGE) {
                String cachedData = redisService.getValue(RedisTemplate.PREFIX_LIST_CATEGORY_DISPLAY_HOMEPAGE.getValue());
                if (cachedData != null && !cachedData.isEmpty()) {
                    try {
                        CategoryDisplayDto[] cachedCategories = objectMapper.readValue(cachedData, CategoryDisplayDto[].class);
                        return List.of(cachedCategories);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            categoryDisplayDtos = categoryDisplayRepository.findAllByPositionOrderByDisplayOrderAsc(categoryDisplayPosition);
        }
        if (categoryDisplayDtos != null && !categoryDisplayDtos.isEmpty()) {
            return displayCategoryMapper.toCategoryDisplayDtos(categoryDisplayDtos);
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void updateCategoryDisplay(List<CategoryDisplayRequestDto> categoryDisplayRequestDtos) {
            Map<Boolean, List<CategoryDisplayRequestDto>> partitionedDtos = categoryDisplayRequestDtos.stream()
                    .collect(Collectors.partitioningBy(dto -> dto.getId() != null && !dto.getId().isEmpty()));

            List<CategoryDisplayRequestDto> updates = partitionedDtos.get(true);
            List<CategoryDisplayRequestDto> creates = partitionedDtos.get(false);

            if (!updates.isEmpty()) {
                List<String> updateIds = updates.stream().map(CategoryDisplayRequestDto::getId).toList();

                Map<String, DisplayCategory> displayMap = categoryDisplayRepository.findAllById(updateIds).stream()
                        .collect(Collectors.toMap(DisplayCategory::getId, Function.identity()));

                for (CategoryDisplayRequestDto dto : updates) {
                    DisplayCategory displayCategory = displayMap.get(dto.getId());
                    if (displayCategory != null) {
                        displayCategoryMapper.updateDisplayCategoryFields(displayCategory, dto);
                    }
                }
                categoryDisplayRepository.saveAll(displayMap.values());
                updateRedisCategory();
            }

            if (!creates.isEmpty()) {
                List<String> categoryIds = creates.stream()
                        .map(CategoryDisplayRequestDto::getCategoryId)
                        .filter(id -> id != null && !id.isEmpty())
                        .distinct()
                        .toList();

                Map<String, Category> categoryMap = categoryRepository.findAllByIdIn(categoryIds).stream()
                        .collect(Collectors.toMap(Category::getId, Function.identity()));

                List<DisplayCategory> newDisplayCategories = new ArrayList<>();
                for (CategoryDisplayRequestDto dto : creates) {
                    Category category = categoryMap.get(dto.getCategoryId());
                    if (category != null) {
                        DisplayCategory newDisplay = DisplayCategory.builder()
                                .category(category)
                                .build();
                        displayCategoryMapper.updateDisplayCategoryFields(newDisplay, dto);
                        newDisplayCategories.add(newDisplay);
                    }
                }
                categoryDisplayRepository.saveAll(newDisplayCategories);
                updateRedisCategory();
            }
    }


    private void updateRedisCategory() {
        try {
            List<DisplayCategory> displayCategories = categoryDisplayRepository.findAllByPositionOrderByDisplayOrderAsc(CategoryDisplayPosition.HOMEPAGE);
            List<CategoryDisplayDto> categoryDisplayDtos = displayCategoryMapper.toCategoryDisplayDtos(displayCategories);
            redisService.addValue(RedisTemplate.PREFIX_LIST_CATEGORY_DISPLAY_HOMEPAGE.getValue(), objectMapper.writeValueAsString(categoryDisplayDtos));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteCategoriesDisplay(List<String> ids) {
        List<DisplayCategory> displayCategories = categoryDisplayRepository.findAllById(ids);
        if (displayCategories.isEmpty() || ids.size() != displayCategories.size()) {
            throw new BadRequestException("SOME_CATEGORY_DISPLAYS_NOT_FOUND");
        }
        categoryDisplayRepository.deleteAll(displayCategories);
    }
}
