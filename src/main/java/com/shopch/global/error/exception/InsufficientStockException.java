package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
