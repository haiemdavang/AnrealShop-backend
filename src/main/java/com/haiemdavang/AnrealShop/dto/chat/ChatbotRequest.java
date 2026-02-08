package com.haiemdavang.AnrealShop.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRequest {
    @NotBlank(message = "Chat input is required")
    private String chatInput;
}
