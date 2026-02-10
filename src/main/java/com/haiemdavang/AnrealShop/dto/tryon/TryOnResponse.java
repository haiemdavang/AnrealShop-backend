package com.haiemdavang.AnrealShop.dto.tryon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TryOnResponse {

    private String resultImageBase64;

    private String mimeType;

    private boolean success;

    private String message;

    public static TryOnResponse success(String resultImageBase64, String mimeType) {
        return TryOnResponse.builder()
                .resultImageBase64(resultImageBase64)
                .mimeType(mimeType)
                .success(true)
                .message("Virtual try-on completed successfully")
                .build();
    }

    public static TryOnResponse error(String message) {
        return TryOnResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
