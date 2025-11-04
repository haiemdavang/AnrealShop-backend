package com.haiemdavang.AnrealShop.modal.entity.category;

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
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "categories")
public class Category {
    @Id
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String urlSlug;

    @Column
    private int level;

    @Column(columnDefinition = "TEXT")
    private String urlPath;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    @Column(name = "is_visible", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isVisible;

    @Column(name = "has_children", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean hasChildren;

    @Column(name = "product_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int productCount;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<ShopCategoryItem> shopCategoryItems;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<DisplayCategory> displayCategories;
}
