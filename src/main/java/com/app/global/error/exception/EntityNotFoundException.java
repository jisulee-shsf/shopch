package com.app.global.error.exception;

import com.app.global.error.ErrorType;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorType errorType) {
        super(errorType);
    }
}
