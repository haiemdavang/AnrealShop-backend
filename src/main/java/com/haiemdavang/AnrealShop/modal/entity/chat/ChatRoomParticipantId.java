package com.haiemdavang.AnrealShop.modal.entity.chat;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class ChatRoomParticipantId implements Serializable {

    @Column(name = "id_phong_chat", nullable = false, length = 36)
    private String chatRoomId;

    @Column(name = "id_nguoi_dung", nullable = false, length = 36)
    private String userId;
}