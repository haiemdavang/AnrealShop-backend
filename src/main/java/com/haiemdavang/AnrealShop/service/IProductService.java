package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.product.*;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IProductService {
    void createProduct(@Valid BaseProductRequest baseProductRequest);
    List<String> suggestMyProductsName(String keyword);
    List<ProductStatusDto> getFilterMeta();

    MyShopProductListResponse getMyShopProducts(int page, int limit, String status, String search, String categoryId, String sortBy);

    void updateProduct(String id, BaseProductRequest baseProductRequest);

    void delete(String id, boolean isForce);

    void delete(Set<String> ids, boolean isForce);

    void updateProductVisible(String id, boolean visible);

    void updateProductVisible(Set<String> ids, boolean visible);

    BaseProductRequest getMyShopProductById(String id);

    MyShopProductListResponse getMyShopProductsForAdmin(int page, int limit, String status, String search, LocalDate startDate, LocalDate endDate);

    void rejectProduct(String id, String reason);

    void approveProduct(String id);

    List<ProductStatusDto> getFilterMetaForAdmin(LocalDate startDate, LocalDate endDate);

    ProductDetailDto getProductById(String id, boolean isReview);

    List<UserProductDto> getProducts(int page, int limit, String search, String categoryId, String sortBy, Double minPrice, Double maxPrice, int rating, List<String> brands,  List<String> colors, List<String> sizes, List<String> origins, List<String> genders);

    List<ProductSku> findByProductSkuIdIn(Set<String> ids);

    void decreaseProductSkuQuantity(Set<OrderItem> orderItems);

    List<ProductSku> getProductSkuByIdIn(Set<String> strings);
}
