package com.app.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNSUPPORTED_OAUTH_PROVIDER(BAD_REQUEST, "AUTH-001", "지원하지 않는 OAuth 제공자입니다."),
    MISSING_AUTHORIZATION_HEADER(BAD_REQUEST, "AUTH-002", "Authorization 헤더가 존재하지 않습니다."),
    INVALID_AUTHORIZATION_HEADER(BAD_REQUEST, "AUTH-003", "유효하지 않은 Authorization 헤더입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "AUTH-004", "만료된 토큰입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "AUTH-005", "유효하지 않은 토큰입니다."),
    INVALID_TOKEN_TYPE(UNAUTHORIZED, "AUTH-006", "유효하지 않은 토큰 타입입니다."),
    INVALID_ROLE(UNAUTHORIZED, "AUTH-007", "유효하지 않은 회원 역할입니다."),
    EXPIRED_REFRESH_TOKEN(UNAUTHORIZED, "AUTH-008", "만료된 리프레시 토큰입니다."),
    ORDER_CANCELLATION_DENIED(FORBIDDEN, "AUTH-009", "주문을 취소할 수 없습니다."),

    REFRESH_TOKEN_NOT_FOUND(NOT_FOUND, "TOKEN-001", "리프레시 토큰을 찾을 수 없습니다."),

    ALREADY_REGISTERED_MEMBER(BAD_REQUEST, "MEMBER-001", "이미 등록된 회원입니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER-002", "회원을 찾을 수 없습니다."),

    PRODUCT_NOT_FOUND(NOT_FOUND, "PRODUCT-001", "상품을 찾을 수 없습니다."),
    OUT_OF_STOCK(BAD_REQUEST, "PRODUCT-002", "상품 재고 수량이 부족합니다."),

    ALREADY_CANCELED_ORDER(BAD_REQUEST, "ORDER-001", "이미 취소된 주문입니다."),
    ORDER_NOT_FOUND(NOT_FOUND, "ORDER-002", "주문을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    }
