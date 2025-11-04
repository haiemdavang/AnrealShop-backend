package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.NoticeScope;
import com.haiemdavang.AnrealShop.dto.chat.ChatMessageRequest;
import com.haiemdavang.AnrealShop.dto.notice.SimpleNoticeMessage;
import com.haiemdavang.AnrealShop.tech.kafka.producer.NoticeKafkaProducer;
import com.haiemdavang.AnrealShop.dto.notice.NoticeTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final NoticeKafkaProducer noticeKafkaProducer;

    @MessageMapping("/chat.send")
//    @SendTo("/topic/public")
    public void sendMessage(@Payload ChatMessageRequest chatMessage, Principal principal) {
        log.warn(chatMessage.toString());
//        SimpleNoticeMessage simpleNoticeMessage = NoticeTemplate.toUser(principal.getName(), "hello");
//        simpleNoticeMessage.setNoticeScope(NoticeScope.USER);
//        noticeKafkaProducer.sendNoticeSyncMessage(simpleNoticeMessage);
    }

}
