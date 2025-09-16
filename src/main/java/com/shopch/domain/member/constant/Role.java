package com.shopch.domain.member.constant;

import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthenticationException;

import java.util.Arrays;

public enum Role {

    USER,
    ADMIN;

    public static Role from(String role) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_ROLE));
    }
}
