package com.haiemdavang.AnrealShop.dto.user;

import com.haiemdavang.AnrealShop.modal.enums.GenderType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    
    @NotBlank(message = "FULLNAME_NOTBLANK")
    @Size(max = 100, message = "FULLNAME_SIZE")
    private String fullName;
    
    @Pattern(regexp = "^(\\+\\d{1,3})?\\s?\\d{10,15}$", message = "PHONENUMBER_PATTERN")
    private String phoneNumber;
    
    @NotNull(message = "GENDER_NOTNULL")
    private GenderType gender;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "DOB_PAST")
    private LocalDate dob;
    
    private String avatarUrl;
}
