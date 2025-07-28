package com.app.global.jwt.constant;

public enum TokenType {

    ACCESS,
    REFRESH;

    public static TokenType from(String tokenType) {
        return TokenType.valueOf(tokenType.toUpperCase());
    }

    public boolean isDifferent(TokenType tokenType) {
        return this != tokenType;
    }
}
