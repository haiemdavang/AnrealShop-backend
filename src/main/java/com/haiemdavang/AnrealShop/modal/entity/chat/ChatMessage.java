package com.haiemdavang.AnrealShop.modal.entity.chat;


import com.haiemdavang.AnrealShop.modal.enums.MessageType;
import com.haiemdavang.AnrealShop.modal.enums.SenderRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"room"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "tin_nhan")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_tin_nhan", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_chat", nullable = false)
    private ChatRoom room;

    @Enumerated(EnumType.STRING)
    @Column(name = "ben_gui", nullable = false)
    private SenderRole senderRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_tin_nhan", nullable = false)
    private MessageType type;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String content;

    @Column(name = "da_doc", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead = false;

    @CreationTimestamp
    @Builder.Default
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}