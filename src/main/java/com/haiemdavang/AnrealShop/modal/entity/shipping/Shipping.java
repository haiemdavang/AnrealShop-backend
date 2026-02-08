package com.haiemdavang.AnrealShop.modal.entity.shipping;


import com.haiemdavang.AnrealShop.modal.entity.address.ShopAddress;
import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"addressFrom", "addressTo", "trackingHistory", "shopOrder"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "van_chuyen")
public class Shipping {

    @Id
    @Column(name = "id_van_chuyen", length = 36, updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_chi_shop", nullable = false)
    private ShopAddress addressFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_chi_nguoi_dung", nullable = false)
    private UserAddress addressTo;

    @Builder.Default
    @Column(name = "ten_don_vi_giao", length = 100, nullable = false)
    private String shipperName = "";

    @Builder.Default
    @Column(name = "sdt_don_vi_giao", length = 20, nullable = false)
    private String shipperPhone  = "";

    @Builder.Default
    @Column(name = "tong_khoi_luong", nullable = false)
    private Long totalWeight = 0L;

    @Column(name = "phi_van_chuyen", nullable = false)
    private Long fee;

    @Builder.Default
    @Column(name = "ghi_chu")
    private String note  = "";

    @Builder.Default
    @Column(name = "da_in", nullable = false)
    private boolean isPrinted  = false;

    @Builder.Default
    @Column(name = "ngay_lay_hang", nullable = false)
    private LocalDate dayPickup  = LocalDate.now().plusDays(1);

    @Column(name = "ly_do_huy", length = 500)
    private String cancelReason;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private ShippingStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_hang_cua_hang", referencedColumnName = "id_don_hang_cua_hang", nullable = false, unique = true)
    private ShopOrder shopOrder;

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ShippingTrack> trackingHistory = new HashSet<>();

    public void addTrackingHistory(ShippingTrack track) {
        if (trackingHistory == null) trackingHistory = new HashSet<>();
        trackingHistory.add(track);
        track.setShipping(this);
    }

    public void setStatus(ShippingStatus status, String note) {
        this.status = status;
        ShippingTrack track = new ShippingTrack(this, status, LocalDateTime.now(), note);
        addTrackingHistory(track);
    }

}
