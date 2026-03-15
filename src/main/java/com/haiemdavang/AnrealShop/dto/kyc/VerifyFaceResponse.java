package com.haiemdavang.AnrealShop.dto.kyc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyFaceResponse {

    private boolean matched;
    private double confidence;
    private String message;
    private int idFaceCount;
    private int selfieFaceCount;
}
