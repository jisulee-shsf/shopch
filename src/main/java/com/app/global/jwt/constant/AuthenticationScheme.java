package com.app.global.jwt.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationScheme {

    BEARER("Bearer");

    private static final String SPACE = " ";

    private final String text;

    public String getPrefix() {
        return text + SPACE;
    }
}
