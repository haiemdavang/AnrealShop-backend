package com.haiemdavang.AnrealShop.dto.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleAddressDto {
    private String id;
    private String nameDisplay;
}
