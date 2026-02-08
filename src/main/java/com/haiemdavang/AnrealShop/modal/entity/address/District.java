package com.haiemdavang.AnrealShop.modal.entity.address;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"province", "wards"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "huyen")
public class District {

    @Id
    @Column(name = "id_huyen", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ten_huyen", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinh", nullable = false)
    private Province province;

// @OneToMany(mappedBy = "district", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
// private Set<Ward> wards;
}