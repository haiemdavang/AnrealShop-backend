package com.haiemdavang.AnrealShop.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatbotResponse {
    private String message;
    private String type;
    private String queryType;
    private String imageUrl;
    private List<String> imageUrls;
    private String productLink;
}
