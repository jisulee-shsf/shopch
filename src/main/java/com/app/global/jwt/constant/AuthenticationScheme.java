package com.app.global.jwt.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthenticationScheme {

    BEARER("Bearer");

    private final String text;
}
