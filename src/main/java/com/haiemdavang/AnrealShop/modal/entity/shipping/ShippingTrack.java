package com.haiemdavang.AnrealShop.modal.entity.shipping;

import com.haiemdavang.AnrealShop.modal.enums.ShippingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "shipping")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "theo_doi_van_chuyen")
public class ShippingTrack {

    @EmbeddedId
    private ShippingTrackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shippingId")
    @JoinColumn(name = "id_van_chuyen", insertable = false, updatable = false)
    private Shipping shipping;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "trang_thai", nullable = false)
    private ShippingStatus status = ShippingStatus.ORDER_CREATED;

    @Column(name = "ghi_chu")
    private String note;

    public LocalDateTime getUpdatedAt() {
        return (this.id != null) ? this.id.getUpdatedAt() : null;
    }

    public ShippingTrack(Shipping shipping, ShippingStatus status, LocalDateTime updatedAt, String note) {
        this.id = new ShippingTrackId(shipping.getId(), updatedAt);
        this.shipping = shipping;
        this.status = status;
        this.note = note;
    }

    public ShippingTrack(Shipping shipping) {
        this.id = new ShippingTrackId(shipping.getId(), LocalDateTime.now());
        this.shipping = shipping;
        this.status = ShippingStatus.ORDER_CREATED;
    }


}