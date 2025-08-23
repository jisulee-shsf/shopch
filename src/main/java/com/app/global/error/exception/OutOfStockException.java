package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class OutOfStockException extends BusinessException {

    public OutOfStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
