package com.haiemdavang.AnrealShop.utils;

import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatRoom;
import com.haiemdavang.AnrealShop.modal.enums.SenderRole;

public class ChatUtils {

    private ChatUtils() {
    }

    public static boolean isShopOwner(ChatRoom room, String userId) {
        return room.getShop().getUser().getId().equals(userId);
    }

    public static SenderRole determineSenderRole(ChatRoom room, String userId) {
        if (room.getCustomer().getId().equals(userId)) {
            return SenderRole.USER;
        } else if (room.getShop().getUser().getId().equals(userId)) {
            return SenderRole.SHOP;
        } else {
            throw new ForbiddenException("ACCESS_DENIED_TO_CHAT_ROOM");
        }
    }

    public static void validateChatRoomAccess(ChatRoom room, String userId) {
        boolean isCustomer = room.getCustomer().getId().equals(userId);
        boolean isShopOwner = room.getShop().getUser().getId().equals(userId);

        if (!isCustomer && !isShopOwner) {
            throw new ForbiddenException("ACCESS_DENIED_TO_CHAT_ROOM");
        }
    }

    public static String getReceiverEmail(ChatRoom room, SenderRole senderRole) {
        if (senderRole == SenderRole.USER) {
            return room.getShop().getUser().getEmail();
        } else {
            return room.getCustomer().getEmail();
        }
    }

    public static SenderRole getReceiverRole(SenderRole senderRole) {
        return senderRole == SenderRole.USER ? SenderRole.SHOP : SenderRole.USER;
    }
}

