package com.app.global.error.exception;

import com.app.global.error.ErrorType;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private ErrorType errorType;

    public BusinessException(ErrorType errorType) {
        super(errorType.getErrorMessage());
        this.errorType = errorType;
    }
}
