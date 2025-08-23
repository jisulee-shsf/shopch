package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
