package com.haiemdavang.AnrealShop.modal.entity.chat;

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
@Entity
@Table(name = "lich_su_chatbot")
public class ChatbotHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_lich_su", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @Column(name = "cau_hoi", columnDefinition = "TEXT", nullable = false)
    private String question;

    @Column(name = "tra_loi", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "loai_response", length = 50)
    private String type;

    @Column(name = "loai_query", length = 50)
    private String queryType;

    @Column(name = "anh_dai_dien", length = 500)
    private String imageUrl;

    @Column(name = "link_san_pham", length = 255)
    private String productLink;

    @CreationTimestamp
    @Builder.Default
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
