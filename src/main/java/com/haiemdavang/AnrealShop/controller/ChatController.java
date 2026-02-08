package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.chat.ChatMessageRequest;
import com.haiemdavang.AnrealShop.service.chat.ChatService;
import com.haiemdavang.AnrealShop.service.notice.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final NotificationService notificationService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequest chatMessageRequest, Principal principal) {
        // 1. Save to DB and get response DTO
        ChatService.SaveMessageResult result = chatService.saveMessage(chatMessageRequest, principal.getName());

        // 2. Send to Receiver via WebSocket
        if (result.getReceiverEmail() != null) {
            notificationService.sendChatMessage(result.getReceiverEmail(), result.getMessageForReceiver());
        }

        // 3. Send back to Sender (to confirm sent/update UI with ID)
        notificationService.sendChatMessage(principal.getName(), result.getMessageForSender());
    }

    @MessageMapping("/chat.typing")
    public void sendTyping(@Payload Map<String, Object> payload, Principal principal) {
        String roomId = (String) payload.get("roomId");
        String receiverUsername = chatService.getReceiverUsername(roomId, principal.getName());

        if (receiverUsername != null) {
            notificationService.sendTypingNotification(receiverUsername, payload);
        }
    }

    @MessageMapping("/chat.read")
    public void markRead(@Payload Map<String, String> payload, Principal principal) {
        String roomId = payload.get("roomId");
        chatService.markAsRead(roomId, principal.getName());
    }
}
