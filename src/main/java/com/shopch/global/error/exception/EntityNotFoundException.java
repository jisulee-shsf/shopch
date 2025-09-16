package com.shopch.global.error.exception;

import com.shopch.global.error.ErrorCode;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
