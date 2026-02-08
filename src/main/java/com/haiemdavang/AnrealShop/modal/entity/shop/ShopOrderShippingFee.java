package com.haiemdavang.AnrealShop.modal.entity.shop;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "shopOrder")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "phi_van_chuyen_don_hang_cua_hang")
public class ShopOrderShippingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false, name = "id_don_hang_cua_hang")
    private String id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_don_hang_cua_hang")
    private ShopOrder shopOrder;

    @Column(name = "so_tien")
    private long amount = 0L;
}
