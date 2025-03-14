package com.app.api.login.dto;

import com.app.global.jwt.dto.TokenResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OauthLoginResponse {

    private String grantType;
    private String accessToken;
    private LocalDateTime accessTokenExpirationDateTime;
    private String refreshToken;
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
