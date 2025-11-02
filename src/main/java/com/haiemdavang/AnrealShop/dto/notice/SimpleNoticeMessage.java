package com.haiemdavang.AnrealShop.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleNoticeMessage {
    private NoticeTemplateType noticeTemplateType;
    private String content;
}
