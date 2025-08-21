package com.app.domain.member.constant;

import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;

import java.util.Arrays;

public enum Role {

    USER,
    ADMIN;

    public static Role from(String role) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new AuthenticationException(ErrorType.INVALID_ROLE));
    }
}
