package com.haiemdavang.AnrealShop.modal.entity.chat;


import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"room", "sender"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('TEXT', 'MEDIA')")
    private MessageType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

}