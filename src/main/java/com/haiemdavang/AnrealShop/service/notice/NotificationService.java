package com.haiemdavang.AnrealShop.service.notice;

import com.haiemdavang.AnrealShop.dto.chat.ChatMessageResponse;
import com.haiemdavang.AnrealShop.mapper.NoticeMapper;
import com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification;
import com.haiemdavang.AnrealShop.modal.entity.notification.UserNotification;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.repository.notice.ShopNotificationRepository;
import com.haiemdavang.AnrealShop.repository.notice.UserNotificationRepository;
import com.haiemdavang.AnrealShop.service.order.IShopOrderService;
import com.haiemdavang.AnrealShop.tech.kafka.dto.notice.NoticeMessage;
import com.haiemdavang.AnrealShop.tech.kafka.dto.notice.SimpleNoticeMessage;
import com.haiemdavang.AnrealShop.tech.socket.WebSocketConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ShopNotificationRepository shopNotificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final IShopOrderService shopOrderService;

    private final NoticeMapper noticeMapper;

    // ==================== Chat Methods ====================

    /**
     * Gửi tin nhắn chat đến user qua WebSocket
     */
    public void sendChatMessage(String username, ChatMessageResponse message) {
        messagingTemplate.convertAndSendToUser(
                username,
                WebSocketConfig.DESTINATION_CHAT,
                message
        );
    }

    /**
     * Gửi thông báo typing đến user qua WebSocket
     */
    public void sendTypingNotification(String username, Map<String, Object> payload) {
        messagingTemplate.convertAndSendToUser(
                username,
                WebSocketConfig.DESTINATION_CHAT + "/typing",
                payload
        );
    }

    @Transactional
    public void sendMessage(SimpleNoticeMessage noticeMessage) {
        switch (noticeMessage.getNoticeTemplateType()) {
            case NEW_ORDER_FOR_SHOP -> createAndSendShopNewOrder(noticeMessage);
            case ORDER_SHIPPED -> createAndSendOrderPickup(noticeMessage);
            case ORDER_DELIVERING -> createAndSendOrderDelivering(noticeMessage);
        }
    }

    private void createAndSendOrderDelivering(SimpleNoticeMessage noticeMessage) {
        ShopOrder shopOrder = shopOrderService.getShopOrderById(noticeMessage.getContent());

        var user = shopOrder.getUser();
        UserNotification userNotification = UserNotification.buildOrderDelivering(user, noticeMessage.getContent());
        userNotification = userNotificationRepository.save(userNotification);
        NoticeMessage noticeResponseUser = noticeMapper.toNoticeMessage(userNotification, user.getId());
        sendToUser(user.getEmail(), noticeResponseUser);
    }

    private void createAndSendOrderPickup(SimpleNoticeMessage noticeMessage) {
        ShopOrder shopOrder = shopOrderService.getShopOrderById(noticeMessage.getContent());
        var shop = shopOrder.getShop();
        ShopNotification shopNotification = ShopNotification.buildPickupOrder(shop, noticeMessage.getContent());
        shopNotification = shopNotificationRepository.save(shopNotification);
        NoticeMessage noticeResponse = noticeMapper.toNoticeMessage(shopNotification, shop.getUser().getId());
        sendToUser(shop.getUser().getEmail(), noticeResponse);
        
        var user = shopOrder.getUser();
        UserNotification userNotification = UserNotification.buildPickupOrder(user, noticeMessage.getContent());
        userNotification = userNotificationRepository.save(userNotification);
        NoticeMessage noticeResponseUser = noticeMapper.toNoticeMessage(userNotification, user.getId());
        sendToUser(user.getEmail(), noticeResponseUser);
    }

    private void createAndSendShopNewOrder(SimpleNoticeMessage noticeMessage) {
        List<ShopOrder> shopOrders = shopOrderService.getShopOrdersByOrderId(noticeMessage.getContent());
        for (ShopOrder shopOrder : shopOrders) {
            var shop = shopOrder.getShop();
            ShopNotification shopNotification = ShopNotification.newOrderForShop(shop, noticeMessage.getContent());
            shopNotification = shopNotificationRepository.save(shopNotification);
            NoticeMessage noticeResponse = noticeMapper.toNoticeMessage(shopNotification, shop.getUser().getId());
            sendToUser(shop.getUser().getEmail(), noticeResponse);
        }
    }


    private void sendToUser(String username, NoticeMessage notification) {
        messagingTemplate.convertAndSendToUser(
                username,
                WebSocketConfig.DESTINATION_NOTICE,
                notification
        );
    }
}
