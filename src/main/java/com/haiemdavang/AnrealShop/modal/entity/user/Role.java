package com.haiemdavang.AnrealShop.modal.entity.user;

import com.haiemdavang.AnrealShop.modal.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "vai_tro", indexes = {
        @Index(name = "idx_vaitro_ten", columnList = "ten_vai_tro", unique = true)
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_vai_tro", length = 36, updatable = false, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ten_vai_tro", nullable = false, unique = true, length = 10, columnDefinition = "ENUM('USER', 'ADMIN')")
    private RoleName name;

    @Column(name = "mo_ta", length = 255)
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<User> users;
}