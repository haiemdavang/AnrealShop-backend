package com.haiemdavang.AnrealShop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends AnrealShopException {
    public ConflictException(String message) {
        super(message);
    }
}
