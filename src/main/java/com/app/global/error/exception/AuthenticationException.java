package com.app.global.error.exception;

import com.app.global.error.ErrorType;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(ErrorType errorType) {
        super(errorType);
    }
}
