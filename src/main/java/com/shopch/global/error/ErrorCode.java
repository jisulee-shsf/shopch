package com.shopch.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_AUTHORIZATION_CODE(UNAUTHORIZED, "AUTH-001", "유효하지 않은 인가 코드입니다."),
    UNSUPPORTED_OAUTH_PROVIDER(BAD_REQUEST, "AUTH-002", "지원하지 않는 OAuth 제공자입니다."),
    MISSING_AUTHORIZATION_HEADER(BAD_REQUEST, "AUTH-003", "Authorization 헤더가 존재하지 않습니다."),
    INVALID_AUTHORIZATION_HEADER(BAD_REQUEST, "AUTH-004", "유효하지 않은 Authorization 헤더입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "AUTH-005", "만료된 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH-006", "유효하지 않은 토큰입니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "AUTH-007", "유효하지 않은 토큰 타입입니다."),
    INVALID_ROLE(UNAUTHORIZED, "AUTH-008", "유효하지 않은 회원 역할입니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "AUTH-009", "만료된 리프레시 토큰입니다."),
    ORDER_ACCESS_DENIED(FORBIDDEN, "AUTH-010", "주문을 취소할 수 없습니다."),

    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "TOKEN-001", "리프레시 토큰을 찾을 수 없습니다."),

    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER-001", "회원을 찾을 수 없습니다."),

    PRODUCT_NOT_FOUND(NOT_FOUND, "PRODUCT-001", "상품을 찾을 수 없습니다."),
    INSUFFICIENT_STOCK(BAD_REQUEST, "PRODUCT-002", "상품 재고 수량이 부족합니다."),

    ORDER_NOT_FOUND(NOT_FOUND, "ORDER-001", "주문을 찾을 수 없습니다."),
    ALREADY_CANCELED_ORDER(BAD_REQUEST, "ORDER-002", "이미 취소된 주문입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
