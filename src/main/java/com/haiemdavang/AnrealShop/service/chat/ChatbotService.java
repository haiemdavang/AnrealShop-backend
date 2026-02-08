package com.haiemdavang.AnrealShop.service.chat;

import com.haiemdavang.AnrealShop.dto.chat.ChatbotHistoryResponse;
import com.haiemdavang.AnrealShop.dto.chat.ChatbotRequest;
import com.haiemdavang.AnrealShop.dto.chat.ChatbotResponse;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatbotHistory;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.chat.ChatbotHistoryRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final RestTemplate restTemplate;
    private final SecurityUtils securityUtils;
    private final ChatbotHistoryRepository chatbotHistoryRepository;

    @Value("${n8n.webhook.base-url:http://localhost:5678}")
    private String n8nBaseUrl;

    @Value("${n8n.webhook.chatbot-path:/webhook/chat-box}")
    private String chatbotPath;

    @Transactional
    public ChatbotResponse askChatbot(ChatbotRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        String userId = currentUser.getId();

        Map<String, String> payload = new HashMap<>();
        payload.put("chatInput", request.getChatInput());
        payload.put("userId", userId);

        log.info("Calling n8n chatbot for user {} with input: {}", userId, request.getChatInput());

        try {
            String url = n8nBaseUrl + chatbotPath;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

            ResponseEntity<ChatbotResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    ChatbotResponse.class
            );

            ChatbotResponse response = responseEntity.getBody();

            log.info("Received chatbot response: type={}, queryType={}",
                    response != null ? response.getType() : "null",
                    response != null ? response.getQueryType() : "null");

            saveHistory(currentUser, request.getChatInput(), response);

            return response;

        } catch (RestClientException e) {
            log.error("Failed to call n8n chatbot: {}", e.getMessage(), e);
            throw new BadRequestException("CHATBOT_UNAVAILABLE");
        } catch (Exception e) {
            log.error("Unexpected error calling n8n chatbot: {}", e.getMessage(), e);
            throw new BadRequestException("CHATBOT_ERROR");
        }
    }

    @Transactional(readOnly = true)
    public Page<ChatbotHistoryResponse> getHistory(int page, int size) {
        User currentUser = securityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        return chatbotHistoryRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(this::toHistoryResponse);
    }

    private void saveHistory(User user, String question, ChatbotResponse response) {
        if (response == null) return;

        ChatbotHistory history = ChatbotHistory.builder()
                .user(user)
                .question(question)
                .answer(response.getMessage())
                .type(response.getType())
                .queryType(response.getQueryType())
                .imageUrl(response.getImageUrl())
                .productLink(response.getProductLink())
                .build();

        chatbotHistoryRepository.save(history);
        log.debug("Saved chatbot history for user {}", user.getId());
    }

    private ChatbotHistoryResponse toHistoryResponse(ChatbotHistory history) {
        return ChatbotHistoryResponse.builder()
                .id(history.getId())
                .question(history.getQuestion())
                .answer(history.getAnswer())
                .type(history.getType())
                .queryType(history.getQueryType())
                .imageUrl(history.getImageUrl())
                .productLink(history.getProductLink())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
