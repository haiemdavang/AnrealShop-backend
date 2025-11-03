package com.haiemdavang.AnrealShop.controller.admin;

import com.haiemdavang.AnrealShop.dto.category.AdminCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryRequestDto;
import com.haiemdavang.AnrealShop.service.ICategoryService;
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
}
