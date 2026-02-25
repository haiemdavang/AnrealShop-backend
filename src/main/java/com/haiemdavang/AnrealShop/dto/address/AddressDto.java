package com.haiemdavang.AnrealShop.dto.address;

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
public class AddressDto {
    private String id;
    private String receiverOrSenderName;
    private String phoneNumber;
    private String detailAddress;
    private String ProvinceId;
    private String DistrictId;
    private String WardId;
    private String ProvinceName;
    private String DistrictName;
    private String WardName;
    private boolean isPrimary;
}
