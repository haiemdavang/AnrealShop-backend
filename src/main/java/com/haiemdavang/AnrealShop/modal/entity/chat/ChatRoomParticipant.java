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
@Table(name = "phong_chat_thanh_vien")
public class ChatRoomParticipant {

    @EmbeddedId
    private ChatRoomParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("chatRoomId")
    @JoinColumn(name = "id_phong_chat")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_nguoi_dung")
    private User user;

    public ChatRoomParticipant(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.id = new ChatRoomParticipantId(chatRoom.getId(), user.getId());
    }
}