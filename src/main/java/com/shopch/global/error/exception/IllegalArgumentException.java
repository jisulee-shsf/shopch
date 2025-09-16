package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class IllegalArgumentException extends BusinessException {

    public IllegalArgumentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
