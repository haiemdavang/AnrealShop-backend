package com.haiemdavang.AnrealShop.tech.n8n;

import com.haiemdavang.AnrealShop.dto.chat.ChatbotResponse;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class N8NService {

    private final RestTemplate restTemplate;

    @Value("${n8n.webhook.base-url:http://localhost:5678}")
    private String n8nBaseUrl;

    @Value("${n8n.webhook.chatbot-path:/webhook/chat-box}")
    private String chatbotPath;

    public ChatbotResponse callChatbotWebhook(String chatInput, String userId) {
        Map<String, String> payload = new HashMap<>();
        payload.put("chatInput", chatInput);
        payload.put("userId", userId);

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

            return responseEntity.getBody();

        } catch (RestClientException e) {
            throw new BadRequestException("CHATBOT_UNAVAILABLE");
        } catch (Exception e) {
            throw new BadRequestException("CHATBOT_ERROR");
        }
    }
}
