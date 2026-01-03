package com.haiemdavang.AnrealShop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnAuthException extends AnrealShopException {
    public UnAuthException(String message) {
        super(message);
    }
}
