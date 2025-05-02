package com.app.global.error.exception;

import com.app.global.error.ErrorType;

public class OutOfStockException extends BusinessException {

    public OutOfStockException(ErrorType errorType) {
        super(errorType);
    }
}
