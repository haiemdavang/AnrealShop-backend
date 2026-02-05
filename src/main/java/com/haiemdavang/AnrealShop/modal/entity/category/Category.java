package com.haiemdavang.AnrealShop.modal.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "danh_muc")
public class Category {
    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_danh_muc", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ten_danh_muc", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc_cha")
    private Category parent;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duong_dan", length = 100)
    private String urlSlug;

    @Column(name = "cap")
    private int level;

    @Column(name = "duong_dan_day_du", columnDefinition = "TEXT")
    private String urlPath;

    @Column(name = "da_xoa", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @Column(name = "hien_thi", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isVisible;

    @Column(name = "co_con", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean hasChildren;

    @Column(name = "tong_san_pham", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int productCount;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<ShopCategoryItem> shopCategoryItems;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<DisplayCategory> displayCategories;
}
