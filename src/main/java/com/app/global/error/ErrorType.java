package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorType {

    MISSING_AUTHORIZATION_HEADER(BAD_REQUEST, "A-001", "Authorization header가 존재하지 않습니다."),
    INVALID_AUTHENTICATION_HEADER_FORMAT(BAD_REQUEST, "A-002", "유효하지 않은 Authorization header 형식입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "A-003", "토큰이 만료되었습니다."),
    INVALID_TOKEN(UNAUTHORIZED, "A-004", "유효하지 않은 토큰입니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "A-005", "유효하지 않은 토큰 타입입니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "A-006", "리프레시 토큰이 만료되었습니다."),

    ALREADY_REGISTERED_MEMBER(BAD_REQUEST, "M-001", "이미 가입된 회원입니다."),
    MEMBER_NOT_FOUND(BAD_REQUEST, "M-002", "회원이 존재하지 않습니다."),

    PRODUCT_NOT_FOUND(BAD_REQUEST, "P-001", "상품이 존재하지 않습니다."),
    OUT_OF_STOCK(BAD_REQUEST, "P-002", "상품 재고 수량이 부족합니다."),

    ORDER_CANCELLATION_DENIED(FORBIDDEN, "O-001", "주문을 취소할 수 없습니다."),
    ALREADY_CANCELED_ORDER(BAD_REQUEST, "O-002", "이미 취소된 주문입니다."),
    ORDER_NOT_FOUND(BAD_REQUEST, "O-003", "주문이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;
}
