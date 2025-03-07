package com.app.global.jwt.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenResponse {

    private String grantType;
    private String accessToken;
    private Date accessTokenExpirationDate;
    private String refreshToken;
    private Date refreshTokenExpirationDate;

    @Builder
    private TokenResponse(String grantType, String accessToken, Date accessTokenExpirationDate, String refreshToken, Date refreshTokenExpirationDate) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.accessTokenExpirationDate = accessTokenExpirationDate;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;
    }
}
