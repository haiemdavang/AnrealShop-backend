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

    @Column(name = "chat_room_id", nullable = false, length = 36)
    private String chatRoomId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;
}