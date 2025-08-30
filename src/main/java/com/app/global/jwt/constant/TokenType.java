package com.app.global.jwt.constant;

import com.app.global.error.ErrorCode;
import com.app.global.error.exception.AuthenticationException;

import java.util.Arrays;

public enum TokenType {

    ACCESS,
    REFRESH;

    public static TokenType from(String tokenType) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(tokenType))
                .findFirst()
                .orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_TOKEN_TYPE));
    }

    public boolean isSame(TokenType tokenType) {
        return this == tokenType;
    }

    public boolean isDifferent(TokenType tokenType) {
        return !isSame(tokenType);
    }

    public boolean isAccess() {
        return this == ACCESS;
    }
}
