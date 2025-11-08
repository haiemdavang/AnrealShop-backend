package com.haiemdavang.AnrealShop.controller.myshop;

import com.haiemdavang.AnrealShop.dto.product.*;
import com.haiemdavang.AnrealShop.service.IProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final IProductService productService;

    @GetMapping("/my-shop/{id}")
    public ResponseEntity<BaseProductRequest> getMyProduct(@PathVariable String id) {
        BaseProductRequest productDto = productService.getMyShopProductById(id);
        return ResponseEntity.ok(productDto);
    }
    @GetMapping("/my-shop")
    public ResponseEntity<MyShopProductListResponse> getMyProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false, defaultValue = "name-asc") String sortBy) {

        MyShopProductListResponse response = productService.getMyShopProducts(page, limit, status, search, categoryId, sortBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<MyShopProductListResponse> getProductsForAdmin(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {

        MyShopProductListResponse response = productService.getMyShopProductsForAdmin(page, limit, status, search, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<?> createProduct(@Valid @RequestBody BaseProductRequest baseProductRequest) {
        productService.createProduct(baseProductRequest);
        return ResponseEntity.ok(Map.of("message", "Product created successfully"));
    }

    @PostMapping("/creates")
    public ResponseEntity<?> createProducts(@Valid @RequestBody List<BaseProductRequest> baseProductRequest) {
        for (BaseProductRequest request : baseProductRequest) {
            productService.createProduct(request);
        }
        return ResponseEntity.ok(Map.of("message", "Product created successfully"));
    }

    @GetMapping("/suggest-my-products-by-name")
    public ResponseEntity<List<String>> suggestMyProductsName(@RequestParam(required = false) String keyword) {
        List<String> productNames = productService.suggestMyProductsName(keyword);
        return ResponseEntity.ok(productNames);
    }

    @GetMapping("/filter-statuses")
    public ResponseEntity<List<ProductStatusDto>> getFilterMeta() {
        List<ProductStatusDto> response = productService.getFilterMeta();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter-statuses-admin")
    public ResponseEntity<List<ProductStatusDto>> getFilterMetaForAdmin(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate endDate) {
        List<ProductStatusDto> response = productService.getFilterMetaForAdmin(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @Valid @RequestBody BaseProductRequest baseProductRequest) {
        productService.updateProduct(id, baseProductRequest);
        return ResponseEntity.ok(Map.of("message", "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id, @RequestParam(required = false) boolean isForce) {
        productService.delete(id, isForce);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteProduct(@RequestBody Set<String> ids, @RequestParam(required = false) boolean isForce) {
        productService.delete(ids, isForce);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    @PutMapping("/{id}/update-visible")
    public ResponseEntity<?> updateProductVisible(@PathVariable String id, @RequestParam(required = false, defaultValue = "0") boolean visible) {
        productService.updateProductVisible(id, visible);
        return ResponseEntity.ok(Map.of("message", "Product visibility updated successfully"));
    }

    @PutMapping("/update-visible-multiple")
    public ResponseEntity<?> updateProductVisible(@RequestBody Set<String> ids, @RequestParam(required = false, defaultValue = "0") boolean visible) {
        productService.updateProductVisible(ids, visible);
        return ResponseEntity.ok(Map.of("message", "Product visibility updated successfully"));
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectProduct(@PathVariable String id, @RequestBody ReasonDto rejectReason) {
        productService.rejectProduct(id, rejectReason.reason());
        return ResponseEntity.ok(Map.of("message", "Product reject successfully"));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveProduct(@PathVariable String id ) {
        productService.approveProduct(id );
        return ResponseEntity.ok(Map.of("message", "Product approve successfully"));
    }
}
