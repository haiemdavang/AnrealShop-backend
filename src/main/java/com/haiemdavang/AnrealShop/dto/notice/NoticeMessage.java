package com.haiemdavang.AnrealShop.dto.notice;

import com.haiemdavang.AnrealShop.dto.NoticeScope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeMessage {
    private String id;
    private String content;
    private String thumbnailUrl;
    private String receiveBy;
    private NoticeScope noticeScope;
    private String redirectUrl;
    private LocalDateTime createdAt;
}
