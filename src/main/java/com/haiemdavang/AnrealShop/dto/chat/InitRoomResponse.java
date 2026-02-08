package com.haiemdavang.AnrealShop.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InitRoomResponse {
    private String roomId;
    private boolean isNew;
    private String shopId;
    private String shopName;
    private String shopAvatar;
}
