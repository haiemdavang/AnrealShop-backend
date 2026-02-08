package com.haiemdavang.AnrealShop.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotHistoryResponse {
    private String id;
    private String question;
    private String answer;
    private String type;
    private String queryType;
    private String imageUrl;
    private String productLink;
    private LocalDateTime createdAt;
}
