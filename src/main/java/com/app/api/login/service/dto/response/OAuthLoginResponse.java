package com.app.api.login.service.dto.response;

import com.app.global.jwt.dto.TokenResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OAuthLoginResponse {

    private String authenticationScheme;
    private String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime accessTokenExpirationDateTime;
    private String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime refreshTokenExpirationDateTime;

    @Builder
    private OAuthLoginResponse(String authenticationScheme, String accessToken, LocalDateTime accessTokenExpirationDateTime, String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.authenticationScheme = authenticationScheme;
        this.accessToken = accessToken;
        this.accessTokenExpirationDateTime = accessTokenExpirationDateTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }

    public static OAuthLoginResponse of(TokenResponse tokenResponse) {
        return OAuthLoginResponse.builder()
                .authenticationScheme(tokenResponse.getAuthenticationScheme())
                .accessToken(tokenResponse.getAccessToken())
                .accessTokenExpirationDateTime(tokenResponse.getAccessTokenExpirationDateTime())
                .refreshToken(tokenResponse.getRefreshToken())
                .refreshTokenExpirationDateTime(tokenResponse.getRefreshTokenExpirationDateTime())
                .build();
    }
}
