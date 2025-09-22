package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
