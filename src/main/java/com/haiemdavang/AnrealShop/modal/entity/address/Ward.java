package com.haiemdavang.AnrealShop.modal.entity.address;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "district")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "wards")
public class Ward {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
}