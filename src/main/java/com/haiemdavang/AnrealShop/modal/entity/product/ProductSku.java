package com.haiemdavang.AnrealShop.modal.entity.product;


import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"product", "attributes"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "san_phamsku", indexes = {
        @Index(name = "idx_sanphamsku_sku_unique", columnList = "ma_sku", unique = true)
})
public class ProductSku {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_san_phamsku", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product product;

    @Column(name = "masku", nullable = false, length = 50)
    private String sku;

    @Column(name = "gia", nullable = false)
    private Long price;

    @Builder.Default
    @Column(name = "da_ban", nullable = false)
    private int sold = 0;

    @Column(name = "so_luong", columnDefinition = "INT DEFAULT 0")
    private int quantity = 0;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "sku_thuoc_tinh",
            joinColumns = @JoinColumn(name = "id_san_phamsku"),
            inverseJoinColumns = @JoinColumn(name = "id_gia_tri_thuoc_tinh")
    )
    private Set<AttributeValue> attributes;

    @Column(name = "anh_dai_dien")
    private String thumbnailUrl;
}