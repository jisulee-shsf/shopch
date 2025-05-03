package com.app.global.error.exception;

import com.app.global.error.ErrorType;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(ErrorType errorType) {
        super(errorType);
    }
}
