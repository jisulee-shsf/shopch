package com.app.global.jwt.constant;

import lombok.Getter;

@Getter
public enum TokenType {

    ACCESS,
    REFRESH;

    public static TokenType from(String tokenType) {
        return TokenType.valueOf(tokenType.toUpperCase());
    }

    public boolean isNotAccess() {
        return this != ACCESS;
    }
}
