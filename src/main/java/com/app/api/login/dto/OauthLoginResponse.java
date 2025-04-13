package com.app.api.login.dto;

import com.app.global.jwt.dto.TokenResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OauthLoginResponse {

    private String grantType;
    private String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime accessTokenExpirationDateTime;
    private String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime refreshTokenExpirationDateTime;

    @Builder
    private OauthLoginResponse(String grantType, String accessToken, LocalDateTime accessTokenExpirationDateTime, String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.accessTokenExpirationDateTime = accessTokenExpirationDateTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }

    public static OauthLoginResponse of(TokenResponse tokenResponse) {
        return OauthLoginResponse.builder()
                .grantType(tokenResponse.getGrantType())
                .accessToken(tokenResponse.getAccessToken())
                .accessTokenExpirationDateTime(tokenResponse.getAccessTokenExpirationDateTime())
                .refreshToken(tokenResponse.getRefreshToken())
                .refreshTokenExpirationDateTime(tokenResponse.getRefreshTokenExpirationDateTime())
                .build();
    }
}
