package com.haiemdavang.AnrealShop.utils;

import com.haiemdavang.AnrealShop.dto.SortEnum;
import org.springframework.data.domain.Sort;

import java.text.Normalizer;

public class ApplicationInitHelper {

    public static String IMAGE_USER_DEFAULT = "https://res.cloudinary.com/dqogp38jb/image/upload/v1750060824/7309681_msx5j1.jpg";

    public static String toSlug(String combined) {
        String noDiacritics = Normalizer.normalize(combined, Normalizer.Form.NFD);
        noDiacritics = noDiacritics.replaceAll("\\p{M}", "");
        String slug = noDiacritics
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
        slug += "-" + System.currentTimeMillis();
        return slug.substring(0, Math.min(slug.length(), 50));
    }

    public static Sort getSortBy(String sortBy) {
        return SortEnum.fromValue(sortBy).getSort();
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];
        if (localPart.length() <= 3) {
            return "***@" + domain;
        }
        String maskedLocal = localPart.substring(0, 3) + "***" + localPart.charAt(localPart.length() - 1);
        return maskedLocal + "@" + domain;
    }

    public static String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return "";
        }
        String digitsOnly = phoneNumber.replaceAll("\\D", "");

        if (digitsOnly.length() <= 6) {
            return "***";
        }
        return digitsOnly.substring(0, 4) + "***" + digitsOnly.substring(digitsOnly.length() - 3);
    }

}
