package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class AlreadyCanceledOrderException extends BusinessException {

    public AlreadyCanceledOrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
