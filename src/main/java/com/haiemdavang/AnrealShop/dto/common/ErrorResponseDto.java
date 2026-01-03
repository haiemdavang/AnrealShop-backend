package com.haiemdavang.AnrealShop.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    @Builder.Default
    private String code = "500";
    @Builder.Default
    private String message = "Lỗi hệ thống không xác định.";
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
    @Builder.Default
    private String traceId = java.util.UUID.randomUUID().toString();
    private List<ItemError> details;
}
