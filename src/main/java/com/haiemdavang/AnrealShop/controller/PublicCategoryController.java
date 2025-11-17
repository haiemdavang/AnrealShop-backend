package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.category.CategoryDisplayDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;
import com.haiemdavang.AnrealShop.service.IDisplayCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/categories")
public class PublicCategoryController {
    private final IDisplayCategoryService displayCategoryService;

    @GetMapping()
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

}
