package com.app.global.jwt.dto;

import com.app.domain.member.entity.Member;
import com.app.domain.token.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenResponse {

    private String authenticationScheme;
    private String accessToken;
    private LocalDateTime accessTokenExpirationDateTime;
    private String refreshToken;
    private LocalDateTime refreshTokenExpirationDateTime;

    @Builder
    private TokenResponse(String authenticationScheme, String accessToken, LocalDateTime accessTokenExpirationDateTime, String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.authenticationScheme = authenticationScheme;
        this.accessToken = accessToken;
        this.accessTokenExpirationDateTime = accessTokenExpirationDateTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }

    public RefreshToken toEntity(Member member) {
        return RefreshToken.create(member, refreshToken, refreshTokenExpirationDateTime);
    }
}
