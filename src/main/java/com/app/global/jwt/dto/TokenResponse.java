package com.app.global.jwt.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenResponse {

    private String grantType;
    private String accessToken;
    private LocalDateTime accessTokenExpirationDateTime;
    private String refreshToken;
    private LocalDateTime refreshTokenExpirationDateTime;

    @Builder
    private TokenResponse(String grantType, String accessToken, LocalDateTime accessTokenExpirationDateTime, String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.accessTokenExpirationDateTime = accessTokenExpirationDateTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }
}
