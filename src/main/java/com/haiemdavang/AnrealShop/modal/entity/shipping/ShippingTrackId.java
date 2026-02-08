package com.haiemdavang.AnrealShop.modal.entity.shipping;

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
public class ShippingTrackId implements Serializable {

    @Column(name = "id_van_chuyen", nullable = false, length = 36)
    private String shippingId;

    @Column(name = "cap_nhat_luc", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}