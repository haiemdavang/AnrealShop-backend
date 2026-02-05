package com.haiemdavang.AnrealShop.modal.entity.order;

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
public class OrderItemTrackId implements Serializable {

    @Column(name = "id_item_don_hang", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "cap_nhat_luc", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}