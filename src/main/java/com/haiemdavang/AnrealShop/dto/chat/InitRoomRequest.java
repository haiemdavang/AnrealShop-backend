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
public class InitRoomRequest {
    @NotBlank(message = "SHOP_ID_REQUIRED")
    private String shopId;
}
