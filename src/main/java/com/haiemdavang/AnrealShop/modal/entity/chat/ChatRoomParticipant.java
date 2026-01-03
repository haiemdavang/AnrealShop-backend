package com.haiemdavang.AnrealShop.modal.entity.chat;

import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"chatRoom", "user"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "chat_room_participants")
public class ChatRoomParticipant {

    @EmbeddedId
    private ChatRoomParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomId")
    @JoinColumn(name = "chat_room_id", insertable = false, updatable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public ChatRoomParticipant(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.id = new ChatRoomParticipantId(chatRoom.getId(), user.getId());
    }
}