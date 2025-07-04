package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    // 인증
    EXPIRED_TOKEN(UNAUTHORIZED, "A-001", "해당 토큰은 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "A-002", "해당 토큰은 유효하지 않습니다."),
    MISSING_AUTHORIZATION_HEADER(UNAUTHORIZED, "A-003", "Authorization 헤더가 없습니다."),
    INVALID_GRANT_TYPE(UNAUTHORIZED, "A-004", "Bearer 인증 타입이 아닙니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "A-005", "해당 리프레시 토큰은 만료되었습니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "A-006", "액세스 토큰 타입이 아닙니다."),

    // 회원
    ALREADY_REGISTERED_MEMBER(BAD_REQUEST, "M-001", "이미 가입된 회원입니다."),
    MEMBER_NOT_FOUND(BAD_REQUEST, "M-002", "회원이 존재하지 않습니다."),

    // 상품
    PRODUCT_NOT_FOUND(BAD_REQUEST, "P-001", "상품이 존재하지 않습니다."),
    OUT_OF_STOCK(BAD_REQUEST, "P-002", "상품 재고 수량이 부족합니다."),

    // 주문
    ORDER_NOT_FOUND(BAD_REQUEST, "O-001", "주문이 존재하지 않습니다."),
    FORBIDDEN_ORDER_CANCELLATION(FORBIDDEN, "O-002", "주문을 취소할 수 없습니다."),
    ALREADY_CANCELED_ORDER(BAD_REQUEST, "O-003", "이미 취소된 주문입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
