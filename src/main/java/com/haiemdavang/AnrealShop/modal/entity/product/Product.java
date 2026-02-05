package com.haiemdavang.AnrealShop.modal.entity.product;

import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import com.haiemdavang.AnrealShop.modal.entity.category.Category;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.enums.RestrictStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Where(clause = "da_xoa = false")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_san_pham", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ten_san_pham", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cua_hang", nullable = false)
    private Shop shop;

    @Column(name = "mo_ta_ngan", columnDefinition = "TEXT")
    private String sortDescription;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    @Column(name = "duong_dan", columnDefinition = "TEXT")
    private String urlSlug;

    @Builder.Default
    @Column(name = "anh_dai_dien", length = 255)
    private String thumbnailUrl = "https://res.cloudinary.com/dlcjc36ow/image/upload/v1747916255/ImagError_jsv7hr.png";

    @Column(name = "gia_goc", nullable = false)
    private Long price;

    @Column(name = "gia_giam", nullable = false)
    private Long discountPrice;

    @Column(name = "so_luong", nullable = false)
    private int quantity = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc")
    private Category category;

    @Column(name = "khoi_luong", nullable = false, columnDefinition = "DECIMAL(10,2) default 0.00")
    private Long weight;
    @Column(name = "chieu_cao", nullable = false, columnDefinition = "DECIMAL(10,2) default 0.00")
    private Long height;
    @Column(name = "chieu_dai", nullable = false, columnDefinition = "DECIMAL(10,2) default 0.00")
    private Long length;
    @Column(name = "chieu_rong", nullable = false, columnDefinition = "DECIMAL(10,2) default 0.00")
    private Long width;

    @Column(name = "doanh_thu", nullable = false)
    private long revenue = 0;

    @Builder.Default
    @Column(name = "da_ban", nullable = false)
    private int sold = 0;

    @Column(name = "diem_danh_giatb", nullable = false)
    private float averageRating = 0;

    @Column(name = "tong_danh_gia", nullable = false)
    private int totalReviews = 0;

    @Builder.Default
    @Column(name = "hien_thi", nullable = false)
    private boolean visible = true;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @Column(name = "bi_han_che", nullable = false)
    private boolean restricted = false;

    @Column(name = "ly_do_han_che", columnDefinition = "TEXT")
    private String restrictedReason;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_han_che", nullable = false)
    private RestrictStatus restrictStatus = RestrictStatus.PENDING;

    @Builder.Default
    @Column(name = "da_xoa", nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ProductMedia> mediaList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductGeneralAttribute> generalAttributes = new HashSet<>();

    public void addGeneralAttribute(AttributeValue attributeValue) {
        ProductGeneralAttribute productAttribute = new ProductGeneralAttribute();
        productAttribute.setProduct(this);
        productAttribute.setAttributeValue(attributeValue);
        productAttribute.setId(new ProductAttributeId(this.id, attributeValue.getId()));
        if(this.generalAttributes == null) {
            this.generalAttributes = new HashSet<>();
        }
        this.generalAttributes.add(productAttribute);
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ProductSku> productSkus;

    public void addMedia(ProductMedia productMedia) {
        if (this.mediaList == null) {
            this.mediaList = new HashSet<>();
        }
        this.mediaList.add(productMedia);
        productMedia.setProduct(this);
    }

}
