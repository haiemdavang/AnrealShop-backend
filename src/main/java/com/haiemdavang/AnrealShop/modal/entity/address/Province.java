package com.haiemdavang.AnrealShop.modal.entity.address;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "districts")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "provinces")
public class Province {

    @Id
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    // @OneToMany(mappedBy = "province", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    // private Set<District> districts;
}