package com.haiemdavang.AnrealShop.dto.wallet;

import com.haiemdavang.AnrealShop.modal.enums.DocumentType;
import com.haiemdavang.AnrealShop.modal.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVerificationDto {
    private String id;
    private String realFullName;
    private String documentNumber;
    private DocumentType documentType;
    private LocalDate dateOfBirth;
    private String frontImageUrl;
    private String backImageUrl;
    private String portraitImageUrl;
    private VerificationStatus status;
    private String rejectionReason;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
