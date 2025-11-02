package com.haiemdavang.AnrealShop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class AdminUserListResponse {
    private Long totalCount;
    private Integer totalPages;
    private Integer currentPage;
    private List<UserManagerDto> users;
}