package com.haiemdavang.AnrealShop.modal.entity.category;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ShopCategoryItemId implements Serializable {

    @Column(name = "shop_categories_id", nullable = false, length = 36)
    private String shopCategoriesId;

    @Column(name = "category_id", nullable = false, length = 36)
    private String categoryId;
}