package com.haiemdavang.AnrealShop.controller.admin;

import com.haiemdavang.AnrealShop.dto.category.AdminCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayRequestDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryRequestDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;
import com.haiemdavang.AnrealShop.service.ICategoryService;
import com.haiemdavang.AnrealShop.service.IDisplayCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminCategoryController {
    private final ICategoryService categoryService;
    private final IDisplayCategoryService displayCategoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories")
    public ResponseEntity<List<AdminCategoryDto>> getCategories() {
        return ResponseEntity.ok(categoryService.getListCategoryForAdmin());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories/disabled")
    public ResponseEntity<List<AdminCategoryDto>> getCategoriesDisabled() {
        return ResponseEntity.ok(categoryService.getListCategoryDisableForAdmin());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.addCategory(categoryRequestDto);
        return ResponseEntity.ok(Map.of("message", "CATEGORY_ADDED_SUCCESSFULLY"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable String categoryId, @RequestBody CategoryRequestDto categoryRequestDto) {
        categoryService.updateCategory(categoryId, categoryRequestDto);
        return ResponseEntity.ok(Map.of("message", "CATEGORY_ADDED_SUCCESSFULLY"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/categories/{categoryId}/switch-status")
    public ResponseEntity<?> switchVisibleCategory(@PathVariable String categoryId, @RequestParam(required = false) boolean includeChildren) {
        categoryService.toggleIsVisible(categoryId, includeChildren);
        return ResponseEntity.ok(Map.of("message", "CATEGORY_DISABLED_SUCCESSFULLY"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<?> softDelete(@PathVariable String categoryId,  @RequestParam(required = false) boolean includeChildren) {
        categoryService.softDelete(categoryId, includeChildren);
        return ResponseEntity.ok(Map.of("message", "CATEGORY_DISABLED_SUCCESSFULLY"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/categories/display")
    public ResponseEntity<List<CategoryDisplayDto>> getCategoriesDisplay(@RequestParam(required = false) String position) {
        CategoryDisplayPosition categoryDisplayPosition = null;
        if (position != null && !position.isEmpty()) {
            try {
                categoryDisplayPosition = CategoryDisplayPosition.valueOf(position.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("INVALID_CATEGORY_DISPLAY_POSITION");
            }
        }
        return ResponseEntity.ok(displayCategoryService.getListCategoriesDisplay(categoryDisplayPosition));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/categories/display")
    public ResponseEntity<?> updateCategoryDisplay(@RequestBody List<CategoryDisplayRequestDto> categoryDisplayRequestDtos) {
        displayCategoryService.updateCategoryDisplay(categoryDisplayRequestDtos);
        return ResponseEntity.ok(Map.of("message", "CATEGORY_DISPLAY_UPDATED_SUCCESSFULLY"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/categories/display")
    public ResponseEntity<?> deleteCategoryDisplay(@RequestBody List<String> ids) {
        displayCategoryService.deleteCategoriesDisplay(ids);
        return ResponseEntity.ok(Map.of("message", "CATEGORY_DISPLAY_UPDATED_SUCCESSFULLY"));
    }
}
