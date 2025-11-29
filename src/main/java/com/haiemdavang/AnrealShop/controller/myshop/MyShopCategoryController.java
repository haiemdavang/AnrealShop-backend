package com.haiemdavang.AnrealShop.controller.myshop;

import com.haiemdavang.AnrealShop.dto.category.BaseCategoryDto;
import com.haiemdavang.AnrealShop.dto.category.CategoryModalSelectedDto;
import com.haiemdavang.AnrealShop.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class MyShopCategoryController {
    private final ICategoryService categoryService;

    @GetMapping("/my-shop")
    public ResponseEntity<List<CategoryModalSelectedDto>> getCategoryMyShop() {
        List<CategoryModalSelectedDto> categories = categoryService.getCategoryMyShop();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<BaseCategoryDto>> getCategorySuggest(@RequestParam(required = false) String keyword) {
        List<BaseCategoryDto> categories = categoryService.getCategorySuggest(keyword);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/suggest-by-product-name")
    public ResponseEntity<Set<BaseCategoryDto>> getCategorySuggestByProductName(@RequestParam(required = false) String keyword) {
        Set<BaseCategoryDto> categories = categoryService.getCategorySuggestByProductName(keyword);
        return ResponseEntity.ok(categories);
    }


}
