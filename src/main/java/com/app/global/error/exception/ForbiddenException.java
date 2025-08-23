package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
