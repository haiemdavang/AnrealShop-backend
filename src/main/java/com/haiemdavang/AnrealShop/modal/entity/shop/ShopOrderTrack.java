package com.haiemdavang.AnrealShop.modal.entity.shop;

import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "shopOrder")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "theo_doi_don_hang_cua_hang")
public class ShopOrderTrack {

    @EmbeddedId
    private ShopOrderTrackId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shopOrderId")
    @JoinColumn(name = "id_don_hang_cua_hang", insertable = false, updatable = false)
    private ShopOrder shopOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false)
    private ShopOrderStatus status = ShopOrderStatus.INIT_PROCESSING;

    public LocalDateTime getUpdatedAt() {
        return (this.id != null) ? this.id.getUpdatedAt() : null;
    }

    public ShopOrderTrack(ShopOrder shopOrder, ShopOrderStatus status, LocalDateTime updatedAt) {
        this.id = new ShopOrderTrackId(shopOrder.getId(), updatedAt);
        this.shopOrder = shopOrder;
        this.status = status;
    }

    public ShopOrderTrack(ShopOrder shopOrder) {
        this.id = new ShopOrderTrackId(shopOrder.getId(), LocalDateTime.now());
        this.shopOrder = shopOrder;
        this.status = ShopOrderStatus.INIT_PROCESSING;
    }
}