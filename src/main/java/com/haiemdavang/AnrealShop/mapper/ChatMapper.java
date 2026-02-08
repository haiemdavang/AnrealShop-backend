package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.chat.ChatMessageResponse;
import com.haiemdavang.AnrealShop.dto.chat.ChatRoomResponse;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatMessage;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatRoom;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.SenderRole;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    /**
     * Convert ChatMessage entity to ChatMessageResponse DTO
     * @param message ChatMessage entity
     * @param myRole vai trò của user hiện tại trong phòng chat
     * @return ChatMessageResponse DTO
     */
    public ChatMessageResponse toMessageResponse(ChatMessage message, SenderRole myRole) {
        if (message == null) return null;

        return ChatMessageResponse.builder()
                .id(message.getId())
                .roomId(message.getRoom().getId())
                .senderRole(message.getSenderRole())
                .type(message.getType())
                .content(message.getContent())
                .isRead(message.isRead())
                .isMe(message.getSenderRole() == myRole)
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Convert ChatRoom to ChatRoomResponse DTO cho customer (người chat với shop)
     * @param room ChatRoom entity
     * @param lastMessage Tin nhắn cuối cùng
     * @param unreadCount Số tin nhắn chưa đọc
     * @return ChatRoomResponse DTO
     */
    public ChatRoomResponse toRoomResponseForCustomer(ChatRoom room, ChatMessage lastMessage, int unreadCount) {
        if (room == null) return null;

        Shop shop = room.getShop();
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .partnerId(shop.getId())
                .partnerName(shop.getName())
                .partnerAvatar(shop.getAvatarUrl())
                .myRole(SenderRole.USER)
                .lastMessage(lastMessage != null ? toMessageResponse(lastMessage, SenderRole.USER) : null)
                .unreadCount(unreadCount)
                .lastActive(room.getLastActive())
                .build();
    }

    /**
     * Convert ChatRoom to ChatRoomResponse DTO cho shop owner (người bán)
     * @param room ChatRoom entity
     * @param lastMessage Tin nhắn cuối cùng
     * @param unreadCount Số tin nhắn chưa đọc
     * @return ChatRoomResponse DTO
     */
    public ChatRoomResponse toRoomResponseForShop(ChatRoom room, ChatMessage lastMessage, int unreadCount) {
        if (room == null) return null;

        User customer = room.getCustomer();
        return ChatRoomResponse.builder()
                .roomId(room.getId())
                .partnerId(customer.getId())
                .partnerName(customer.getFullName() != null ? customer.getFullName() : customer.getUsername())
                .partnerAvatar(customer.getAvatarUrl())
                .myRole(SenderRole.SHOP)
                .lastMessage(lastMessage != null ? toMessageResponse(lastMessage, SenderRole.SHOP) : null)
                .unreadCount(unreadCount)
                .lastActive(room.getLastActive())
                .build();
    }
}
