package com.haiemdavang.AnrealShop.service.notice;

import com.haiemdavang.AnrealShop.dto.notice.NoticeMessage;
import com.haiemdavang.AnrealShop.dto.notice.NoticeMyShopTemplate;
import com.haiemdavang.AnrealShop.dto.notice.NoticeTemplate;
import com.haiemdavang.AnrealShop.dto.notice.SimpleNoticeMessage;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.mapper.NoticeMapper;
import com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.repository.notice.ShopNotificationRepository;
import com.haiemdavang.AnrealShop.repository.notice.UserNotificationRepository;
import com.haiemdavang.AnrealShop.service.IShopService;
import com.haiemdavang.AnrealShop.service.IUserService;
import com.haiemdavang.AnrealShop.service.order.IShopOrderService;
import com.haiemdavang.AnrealShop.tech.kafka.producer.NoticeKafkaProducer;
import com.haiemdavang.AnrealShop.tech.socket.WebSocketConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NoticeKafkaProducer noticeKafkaProducer;
    private final ShopNotificationRepository shopNotificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final IUserService userService;
    private final IShopService shopService;
    private final IShopOrderService shopOrderService;

    private final NoticeMapper noticeMapper;

  
//    @Transactional
//    public void asyncSendNoticeMessage(SimpleNoticeMessage noticeMessage) {
//        switch (noticeMessage.getNoticeScope()) {
//            case USER -> {
//                var user = userService.findByEmail(noticeMessage.getReceiveBy());
//                var userNotification = noticeMapper.toUserNotification(noticeMessage, user);
//                userNotificationRepository.save(userNotification);
//            }
//            case SHOP -> {
//                var shop = shopService.findByEmailUser(noticeMessage.getReceiveBy());
//                var shopNotification = noticeMapper.toShopNotification(noticeMessage, shop);
//                shopNotificationRepository.save(shopNotification);
//            }
//            case ADMIN -> {
//
//            }
//            case PUBLIC -> {
//            }
//            default -> {
//                log.error(noticeMessage.getNoticeScope().toString());
//                throw new AnrealShopException("INVALID_NOTICE_SCOPE");
//            }
//        }
//        noticeKafkaProducer.sendNoticeSyncMessage(noticeMessage);
//    }

    @Transactional
    public void sendMessage(SimpleNoticeMessage noticeMessage) {
        switch (noticeMessage.getNoticeTemplateType()) {
            case NEW_ORDER_FOR_SHOP -> {
                createAndSendShopNewOrder(noticeMessage);
            }

//            case USER, SHOP -> {
//                sendToUser(noticeMessage.getReceiveBy(), noticeMessage);
//                var user = userService.findByEmail(noticeMessage.getReceiveBy());
//                var userNotification = noticeMapper.toUserNotification(noticeMessage, user);
//                userNotificationRepository.save(userNotification);
//            }
//            case ADMIN -> {
//            }
//            case PUBLIC -> {
//
//            }
//            default -> {
//                log.error(noticeMessage.getNoticeScope().toString());
//                throw new AnrealShopException("INVALID_NOTICE_SCOPE");
//            }
        }
    }

    private void createAndSendShopNewOrder(SimpleNoticeMessage noticeMessage) {
        List<ShopOrder> shopOrders = shopOrderService.getShopOrdersByOrderId(noticeMessage.getContent());
        for (ShopOrder shopOrder : shopOrders) {
            var shop = shopOrder.getShop();
            ShopNotification shopNotification = NoticeMyShopTemplate.newOrderForShop(shop, noticeMessage.getContent());
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

    private void sendToTopic(String topic, NoticeMessage notification) {
        messagingTemplate.convertAndSend(
                WebSocketConfig.DESTINATION_PUBLIC + topic,
                notification
        );
    }
}
