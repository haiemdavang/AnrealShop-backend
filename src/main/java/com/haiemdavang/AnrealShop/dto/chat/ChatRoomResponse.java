package com.haiemdavang.AnrealShop.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.modal.enums.SenderRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomResponse {
    private String roomId;
    private String partnerId;
    private String partnerName;
    private String partnerAvatar;
    private SenderRole myRole;
    private ChatMessageResponse lastMessage;
    private int unreadCount;
    private LocalDateTime lastActive;
}
