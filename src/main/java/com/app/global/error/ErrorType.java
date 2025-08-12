package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    UNSUPPORTED_OAUTH_PROVIDER(BAD_REQUEST, "A-001", "지원하지 않는 OAuth 제공자입니다."),
    MISSING_AUTHORIZATION_HEADER(BAD_REQUEST, "A-002", "Authorization 헤더가 존재하지 않습니다."),
    INVALID_AUTHORIZATION_HEADER_FORMAT(BAD_REQUEST, "A-003", "유효하지 않은 Authorization 헤더 형식입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "A-004", "만료된 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "A-005", "유효하지 않은 토큰입니다."),
    INVALID_CLAIM(UNAUTHORIZED, "A-006", "유효하지 않은 클레임 값입니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "A-007", "유효하지 않은 토큰 타입입니다."),
    TOKEN_TYPE_MISMATCH(UNAUTHORIZED, "A-008", "토큰 타입이 일치하지 않습니다."),
    INVALID_ROLE(UNAUTHORIZED, "A-009", "유효하지 않은 회원 역할입니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "A-010", "만료된 리프레시 토큰입니다."),

    ALREADY_REGISTERED_MEMBER(BAD_REQUEST, "M-001", "이미 가입된 회원입니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "M-002", "회원을 찾을 수 없습니다."),

    PRODUCT_NOT_FOUND(NOT_FOUND, "P-001", "상품을 찾을 수 없습니다."),
    OUT_OF_STOCK(BAD_REQUEST, "P-002", "상품 재고 수량이 부족합니다."),

    ORDER_CANCELLATION_DENIED(FORBIDDEN, "O-001", "주문을 취소할 수 없습니다."),
    ALREADY_CANCELED_ORDER(BAD_REQUEST, "O-002", "이미 취소된 주문입니다."),
    ORDER_NOT_FOUND(NOT_FOUND, "O-003", "주문을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
