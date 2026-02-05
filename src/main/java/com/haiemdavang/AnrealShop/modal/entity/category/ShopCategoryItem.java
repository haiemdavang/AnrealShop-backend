package com.haiemdavang.AnrealShop.modal.entity.category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "danh_muc_cua_hang_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ShopCategoryItem {
    @EmbeddedId
    private ShopCategoryItemId id;

    @MapsId("shopCategoriesId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc_cua_hang", nullable = false)
    private ShopCategory shopCategory;

    @MapsId("categoryId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc", nullable = false)
    private Category category;

    public ShopCategoryItem(ShopCategory shopCategory, Category category) {
        this.shopCategory = shopCategory;
        this.category = category;
        this.id = new ShopCategoryItemId(shopCategory.getId(), category.getId());
    }
}