package com.haiemdavang.AnrealShop.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectRequest {
    @NotBlank(message = "REASON_NOT_BLANK")
    private String reason;
}
