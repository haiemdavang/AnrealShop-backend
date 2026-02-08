package com.haiemdavang.AnrealShop.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.modal.enums.MessageType;
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
public class ChatMessageResponse {
    private String id;
    private String roomId;
    private SenderRole senderRole;
    private MessageType type;
    private String content;
    private boolean isRead;
    private boolean isMe;
    private LocalDateTime createdAt;
}
