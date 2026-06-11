package com.haiemdavang.AnrealShop.service.chat;

import com.haiemdavang.AnrealShop.dto.chat.ChatbotHistoryResponse;
import com.haiemdavang.AnrealShop.dto.chat.ChatbotRequest;
import com.haiemdavang.AnrealShop.dto.chat.ChatbotResponse;
import com.haiemdavang.AnrealShop.modal.entity.chat.ChatbotHistory;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.chat.ChatbotHistoryRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.tech.gemini.AIGenerateService;
import com.haiemdavang.AnrealShop.tech.gemini.PromptType;
import com.haiemdavang.AnrealShop.tech.n8n.N8NService;
import com.haiemdavang.AnrealShop.tech.rag.RAGService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final SecurityUtils securityUtils;
    private final ChatbotHistoryRepository chatbotHistoryRepository;
    private final ProductRepository productRepository;
    private final N8NService n8NService;
    private final RAGService ragService;
    private final AIGenerateService aiGenerateService;

    @Transactional
    public ChatbotResponse askChatbotN8N(ChatbotRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        String userId = currentUser.getId();

        ChatbotResponse response = n8NService.callChatbotWebhook(request.getChatInput(), userId);
        response.setType("text");
        saveHistory(currentUser, request.getChatInput(), response, "text");
        return response;
    }

    @Transactional
    public ChatbotResponse askChatbotEmbed(@Valid ChatbotRequest request) {
        User currentUser = securityUtils.getCurrentUser();
        String vectorString = RAGService.toPgVector(ragService.convertToVector(request.getChatInput()));

        List<String> productIds = productRepository.searchSimilarProductIds(vectorString);
        Set<Product> products = productRepository.findByIdIn(productIds);

        String response = aiGenerateService.generate(PromptType.SUGGEST_TEXT.getTableName(), PromptType.SUGGEST_TEXT.getFieldName(), products.stream().map(Product::toString).collect(Collectors.joining("\n")));
        ChatbotResponse chatbotResponse = ChatbotResponse.builder()
                .message(response)
                .type("html")
                .build();
        saveHistory(currentUser, request.getChatInput(), chatbotResponse, "html");
        return chatbotResponse;
    }

    @Transactional(readOnly = true)
    public Page<ChatbotHistoryResponse> getHistory(int page, int size) {
        User currentUser = securityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);

        return chatbotHistoryRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(this::toHistoryResponse);
    }

    private void saveHistory(User user, String question, ChatbotResponse response, String type) {
        if (response == null) return;

        ChatbotHistory history = ChatbotHistory.builder()
                .user(user)
                .question(question)
                .answer(response.getMessage())
                .type(type)
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
