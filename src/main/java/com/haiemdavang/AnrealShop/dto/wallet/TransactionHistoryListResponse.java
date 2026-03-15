package com.haiemdavang.AnrealShop.dto.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryListResponse {
    private Long totalCount;
    private Integer totalPages;
    private Integer currentPage;
    private List<TransactionHistoryDto> transactions;
}
