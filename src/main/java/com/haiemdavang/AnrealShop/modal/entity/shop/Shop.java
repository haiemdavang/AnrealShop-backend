package com.haiemdavang.AnrealShop.modal.entity.shop;


import com.haiemdavang.AnrealShop.modal.entity.category.ShopCategory;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "shopCategories"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "cua_hang")
@SQLDelete(sql = "UPDATE cua_hang SET da_xoa = true, ngay_cap_nhat = CURRENT_TIMESTAMP WHERE id_cua_hang = ?")
@Where(clause = "da_xoa = false")
public class Shop {

    @Id
    @Column(name = "id_cua_hang", length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ten_cua_hang", nullable = false, length = 100)
    private String name;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duong_dan", columnDefinition = "TEXT")
    private String urlSlug;

    @Column(name = "anh_dai_dien", length = 255)
    private String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "tongsp", columnDefinition = "INT DEFAULT 0")
    private int productCount;

    @Column(name = "doanh_thu", columnDefinition = "BIGINT DEFAULT 0")
    private long revenue;

    @Column(name = "diem_danh_giatb", columnDefinition = "FLOAT DEFAULT 0")
    private float averageRating;

    @Column(name = "tong_danh_gia", columnDefinition = "INT DEFAULT 0")
    private int totalReviews;

    @Column(name = "luot_theo_doi", columnDefinition = "INT DEFAULT 0")
    private int followerCount;

    @Column(name = "da_xoa", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted = false;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ShopCategory> shopCategories;

     @UpdateTimestamp
     @Column(name = "ngay_cap_nhat")
     private LocalDateTime updatedAt;
}