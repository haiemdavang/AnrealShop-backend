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

    @Column(name = "id_danh_muc_cua_hang", nullable = false, length = 36)
    private String shopCategoriesId;

    @Column(name = "id_danh_muc", nullable = false, length = 36)
    private String categoryId;
}