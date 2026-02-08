package com.haiemdavang.AnrealShop.dto.chat;

import com.haiemdavang.AnrealShop.modal.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    private String roomId;
    private String content;
    private MessageType type;

    public MessageType getType() {
        return type != null ? type : MessageType.TEXT;
    }
}
