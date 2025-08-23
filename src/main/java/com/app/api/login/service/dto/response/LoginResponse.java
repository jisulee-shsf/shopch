package com.app.api.login.service.dto.response;

import com.app.global.jwt.dto.TokenPair;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoginResponse {

    private String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime accessTokenExpiresAt;

    private String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime refreshTokenExpiresAt;

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
