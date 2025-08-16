package com.app.global.error.exception;

import com.app.global.error.ErrorType;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(ErrorType errorType) {
        super(errorType);
    }
}
