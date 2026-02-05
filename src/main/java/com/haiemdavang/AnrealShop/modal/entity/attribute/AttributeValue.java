package com.haiemdavang.AnrealShop.modal.entity.attribute;

import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
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
@ToString(exclude = {"attributeKey", "productSkus"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "gia_tri_thuoc_tinh",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_giatrithuoctinh_khoa_giatri", columnNames  = {"id_thuoc_tinh", "gia_tri"})
        }
)
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_gia_tri_thuoc_tinh", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thuoc_tinh", nullable = false)
    private AttributeKey attributeKey;

    @Column(name = "gia_tri", nullable = false)
    private String value;

    @Column(name = "thu_tu_hien_thi", columnDefinition = "INT DEFAULT 0")
    private int displayOrder = 0;

    @Column(name = "mac_dinh", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDefault = false;

    @Column(name = "du_lieu_mo_ta", columnDefinition = "TEXT")
    private String metadata; // Tam thoi bo qua, co the dung den sau nha cac pro

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "attributes", fetch = FetchType.LAZY)
    private Set<ProductSku> productSkus;

}