package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    // 인증
    EXPIRED_TOKEN(UNAUTHORIZED, "A-001", "해당 토큰은 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "A-002", "해당 토큰은 유효하지 않습니다."),
    MISSING_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003", "Authorization 헤더가 없습니다."),
    INVALID_GRANT_TYPE(UNAUTHORIZED, "A-004", "Bearer 인증 타입이 아닙니다."),

    // 회원
    INVALID_MEMBER_TYPE(BAD_REQUEST, "M-001", "잘못된 회원 타입입니다."),
    ALREADY_REGISTERED_MEMBER(BAD_REQUEST, "M-002", "이미 가입된 회원입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
