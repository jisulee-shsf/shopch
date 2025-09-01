package com.shopch.api.auth.service.dto.response;

import com.shopch.global.jwt.dto.TokenPair;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginResponse {

    private final String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime accessTokenExpiresAt;

    private final String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime refreshTokenExpiresAt;

    @Builder
    private LoginResponse(String accessToken, LocalDateTime accessTokenExpiresAt, String refreshToken, LocalDateTime refreshTokenExpiresAt) {
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public static LoginResponse of(TokenPair tokenPair) {
        return LoginResponse.builder()
                .accessToken(tokenPair.getAccessToken())
                .accessTokenExpiresAt(tokenPair.getAccessTokenExpiresAt())
                .refreshToken(tokenPair.getRefreshToken())
                .refreshTokenExpiresAt(tokenPair.getRefreshTokenExpiresAt())
                .build();
    }
}
