package com.haiemdavang.AnrealShop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends AnrealShopException {
    public ForbiddenException(String message) {
        super(message);
    }
}