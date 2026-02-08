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
@Table(name = "xa")
public class Ward {

    @Id
    @Column(name = "id_xa", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ten_xa", nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_huyen", nullable = false)
    private District district;
}