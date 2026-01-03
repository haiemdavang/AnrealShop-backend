package com.haiemdavang.AnrealShop.modal.entity.chat;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"participants", "messages"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "last_active", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lastActive;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ChatRoomParticipant> participants;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> messages;

    @PrePersist
    protected void onCreate() {
        if (lastActive == null) {
            lastActive = LocalDateTime.now();
        }
    }
}
