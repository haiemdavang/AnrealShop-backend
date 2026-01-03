package com.haiemdavang.AnrealShop.modal.entity.category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_category_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ShopCategoryItem {
    @EmbeddedId
    @Column(name = "shop_category_item_id", nullable = false, length = 36)
    private ShopCategoryItemId id;

    @MapsId("shopCategoriesId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_categories_id", nullable = false)
    private ShopCategory shopCategory;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public ShopCategoryItem(ShopCategory shopCategory, Category category) {
        this.shopCategory = shopCategory;
        this.category = category;
        this.id = new ShopCategoryItemId(shopCategory.getId(), category.getId());
    }
}