package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
