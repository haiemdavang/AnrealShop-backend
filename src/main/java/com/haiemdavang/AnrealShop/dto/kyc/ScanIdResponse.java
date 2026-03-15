package com.haiemdavang.AnrealShop.dto.kyc;

import com.haiemdavang.AnrealShop.modal.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanIdResponse {

    private String fullName;
    private String documentNumber;
    private DocumentType documentType;
    private String dateOfBirth;
    private String gender;
    private String nationality;
    private String placeOfOrigin;
    private String placeOfResidence;
    private String expiryDate;
    private String rawText;
}
