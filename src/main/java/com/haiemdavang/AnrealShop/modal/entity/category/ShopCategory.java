package com.haiemdavang.AnrealShop.modal.entity.category;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "shop_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @OneToMany(mappedBy = "shopCategory", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ShopCategoryItem> shopCategoryItems;
}