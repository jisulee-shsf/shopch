package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
