package com.haiemdavang.AnrealShop.dto.wallet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.haiemdavang.AnrealShop.modal.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyWalletRequest {

    @NotBlank(message = "REAL_FULL_NAME_REQUIRED")
    @Size(max = 100, message = "REAL_FULL_NAME_TOO_LONG")
    private String realFullName;

    @NotBlank(message = "DOCUMENT_NUMBER_REQUIRED")
    @Size(max = 20, message = "DOCUMENT_NUMBER_TOO_LONG")
    private String documentNumber;

    @NotNull(message = "DOCUMENT_TYPE_REQUIRED")
    private DocumentType documentType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;

    private String frontImageUrl;

    private String backImageUrl;

    private String portraitImageUrl;

    @NotBlank(message = "PAYMENT_PASSWORD_REQUIRED")
    @Size(min = 6, max = 50, message = "PAYMENT_PASSWORD_LENGTH_INVALID")
    private String paymentPassword;
}
