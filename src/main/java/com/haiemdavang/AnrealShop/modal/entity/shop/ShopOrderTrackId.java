package com.haiemdavang.AnrealShop.modal.entity.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ShopOrderTrackId implements Serializable {

    @Column(name = "id_don_hang_cua_hang", nullable = false, length = 36)
    private String shopOrderId;

    @Column(name = "cap_nhat_luc", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}