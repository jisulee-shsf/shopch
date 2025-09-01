package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class OutOfStockException extends BusinessException {

    public OutOfStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
