package com.haiemdavang.AnrealShop.modal.entity.chat;


import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"customer", "shop", "messages"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "phong_chat", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_nguoi_dung", "id_cua_hang"})
})
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_phong_chat", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "hoat_dong_gan_nhat", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cua_hang", nullable = false)
    private Shop shop;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> messages;

    @PrePersist
    protected void onCreate() {
        if (lastActive == null) {
            lastActive = LocalDateTime.now();
        }
    }
}
