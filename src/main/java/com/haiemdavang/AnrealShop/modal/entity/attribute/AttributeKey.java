package com.haiemdavang.AnrealShop.modal.entity.attribute;

import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
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
@ToString(exclude = {"shop", "attributeValues"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "thuoc_tinh", indexes = {
        @Index(name = "idx_thuoctinh_tenkhoa", columnList = "ten_khoa", unique = true)
})
public class AttributeKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_thuoc_tinh", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ten_khoa", nullable = false, length = 50)
    private String keyName;

    @Column(name = "ten_hien_thi", nullable = false, length = 100)
    private String displayName;

    @Column(name = "mac_dinh", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDefault = false;

    @Column(name = "thu_tu_hien_thi")
    private int displayOrder = 0;

    @Column(name = "chon_nhieu", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isMultiSelected = false;

    @Column(name = "dung_chosku", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isForSku = false;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "cua_hang_thuoc_tinh",
            joinColumns = @JoinColumn(name = "id_thuoc_tinh"),
            inverseJoinColumns = @JoinColumn(name = "id_cua_hang")
    )
    private Set<Shop> shops;
}