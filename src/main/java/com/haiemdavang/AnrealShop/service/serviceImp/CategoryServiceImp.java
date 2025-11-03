package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.category.AdminCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.BaseCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryModalSelectedDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryRequestDto;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsCategory;
import com.haiemdavang.AnrealShop.tech.elasticsearch.service.CategoryIndexerService;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.CategoryMapper;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import com.haiemdavang.AnrealShop.repository.CategoryRepository;
import com.haiemdavang.AnrealShop.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryIndexerService esCategoryIndexerService;
    private final CategoryMapper categoryMapper;

    @Override
    public Category findByIdAndThrow(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("CATEGORY_NOT_FOUND"));
    }

    @Override
    public Category findByIdOrUrlSlug(String categoryId) {
        return categoryRepository.findByIdOrUrlSlug(categoryId, categoryId)
                .orElseThrow(() -> new BadRequestException("CATEGORY_NOT_FOUND"));
    }

    @Override
    public Category findById(String categoryId) {
        return categoryRepository.findById(categoryId)
                .orElse(null);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<BaseCategoryDto> getCategorySuggest(String keyword) {
        List<EsCategory> categories = esCategoryIndexerService.getCategoriesByKeyword(keyword);
        return categories.stream().map(categoryMapper::toBaseCategoryDto)
                .toList();
    }

    @Override
    public Set<BaseCategoryDto> getCategorySuggestByProductName(String keyword) {
        Set<EsCategory> categories = esCategoryIndexerService.getCategoriesByProductName(keyword, null);
        return categories.stream().map(categoryMapper::toBaseCategoryDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Cacheable(value = "categories", key = "#root.method.name")
    public List<CategoryModalSelectedDto> getCategoryMyShop() {
//        Shop shop = securityUtils.getCurrentUserShop();
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryModalSelectedDto)
                .toList();
    }

    @Override
    public List<AdminCategoryDto> getListCategoryForAdmin() {
        return categoryMapper.toAdminCategoryDtoList(categoryRepository.findAllByIsDeletedFalseAndIsVisibleTrueOrderByCreatedAtAsc());
    }

    @Override
    public List<AdminCategoryDto> getListCategoryDisableForAdmin() {
        return categoryMapper.toAdminCategoryDtoList(categoryRepository.findAllByIsDeletedFalseAndIsVisibleFalseOrderByCreatedAtAsc());
    }

    @Override
    @Transactional
    public void addCategory(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toCategoryEntity(categoryRequestDto);

        if (categoryRequestDto.getParentId() != null && !categoryRequestDto.getParentId().isEmpty()) {
            Category parent = findByIdAndThrow(categoryRequestDto.getParentId());
            if (!parent.isHasChildren()){
                parent.setHasChildren(true);
                categoryRepository.save(parent);
            }
            category.setParent(parent);
            category.setUrlPath(parent.getUrlPath() + "/" + categoryRequestDto.getSlug());
        } else {
            category.setUrlPath("/" + categoryRequestDto.getSlug());
        }

        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(String categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = findByIdAndThrow(categoryId);
        categoryMapper.updateCategoryFromDto(categoryRequestDto, category);

        if (categoryRequestDto.getParentId() != null && !categoryRequestDto.getParentId().isEmpty()) {
            Category parent = findByIdAndThrow(categoryRequestDto.getParentId());
            category.setParent(parent);
            category.setUrlPath(parent.getUrlPath() + "/" + categoryRequestDto.getSlug());
        } else {
            category.setParent(null);
            category.setUrlPath("/" + categoryRequestDto.getSlug());
        }
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void toggleIsVisible(String categoryId, boolean isToggleAll) {
        Category category = findByIdAndThrow(categoryId);
        boolean newVisibility = !category.isVisible();
        category.setVisible(newVisibility);

        List<Category> categoriesToUpdate = new ArrayList<>();
        categoriesToUpdate.add(category);

        if (isToggleAll && category.isHasChildren()) {
            List<Category> allCategories = categoryRepository.findAllByIsDeletedFalse();
            Map<String, List<Category>> childrenMap = allCategories.stream()
                    .filter(c -> c.getParent() != null)
                    .collect(Collectors.groupingBy(c -> c.getParent().getId()));
            collectChildrenForVisibilityToggle(categoryId, newVisibility, categoriesToUpdate, childrenMap);
        }

        categoryRepository.saveAll(categoriesToUpdate);
    }

    @Override
    @Transactional
    public void softDelete(String categoryId, boolean includeChildren) {
        Category category = findByIdAndThrow(categoryId);
        category.setDeleted(true);
        category.setVisible(false);

        List<Category> categoriesToUpdate = new ArrayList<>();
        categoriesToUpdate.add(category);

        if (includeChildren && category.isHasChildren()) {
            List<Category> allCategories = categoryRepository.findAllByIsDeletedFalse();
            Map<String, List<Category>> childrenMap = allCategories.stream()
                    .filter(c -> c.getParent() != null)
                    .collect(Collectors.groupingBy(c -> c.getParent().getId()));
            collectChildrenForSoftDelete(categoryId, categoriesToUpdate, childrenMap);
        }

        categoryRepository.saveAll(categoriesToUpdate);
    }

    @Override
    public boolean existsById(String categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public Category getReferenceById(String categoryId) {
        return categoryRepository.getReferenceById(categoryId);
    }


    private void collectChildrenForVisibilityToggle(
            String parentId,
            boolean visibility,
            List<Category> toUpdate,
            Map<String, List<Category>> childrenMap
    ) {
        List<Category> children = childrenMap.getOrDefault(parentId, List.of());

        for (Category child : children) {
            child.setVisible(visibility);
            toUpdate.add(child);

            if (child.isHasChildren()) {
                collectChildrenForVisibilityToggle(child.getId(), visibility, toUpdate, childrenMap);
            }
        }
    }
    private void collectChildrenForSoftDelete(
            String parentId,
            List<Category> toUpdate,
            Map<String, List<Category>> childrenMap
    ) {
        List<Category> children = childrenMap.getOrDefault(parentId, List.of());

        for (Category child : children) {
            child.setDeleted(true);
            child.setVisible(false);
            toUpdate.add(child);

            if (child.isHasChildren()) {
                collectChildrenForSoftDelete(child.getId(), toUpdate, childrenMap);
            }
        }
    }

}
