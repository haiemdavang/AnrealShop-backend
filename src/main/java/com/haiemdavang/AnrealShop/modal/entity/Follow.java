package com.haiemdavang.AnrealShop.modal.entity;


import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "shop"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "theo_doi",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_shop_follow", columnNames = {"id_nguoi_dung", "id_cua_hang"})
        }
)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_theo_doi", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cua_hang", nullable = false)
    private Shop shop;

    @CreationTimestamp
    @Column(name = "thoi_gian_theo_doi", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime followedAt;
}