package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    // 인증
    EXPIRED_TOKEN(UNAUTHORIZED, "A-001", "해당 토큰은 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "A-002", "해당 토큰은 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
