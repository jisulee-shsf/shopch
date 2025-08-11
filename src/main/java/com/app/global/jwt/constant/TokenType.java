package com.app.global.jwt.constant;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;

import java.util.Arrays;

public enum TokenType {

    ACCESS,
    REFRESH;

    public static TokenType from(String tokenType) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(tokenType))
                .findAny()
                .orElseThrow(() -> new AuthenticationException(ErrorType.INVALID_TOKEN_TYPE));
    }

    public boolean isDifferent(TokenType tokenType) {
        return this != tokenType;
    }
}
