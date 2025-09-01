package com.shopch.global.jwt.dto;

import com.shopch.domain.member.entity.Member;
import com.shopch.domain.token.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenPair {

    private final String accessToken;
    private final LocalDateTime accessTokenExpiresAt;
    private final String refreshToken;
    private final LocalDateTime refreshTokenExpiresAt;

    @Builder
    private TokenPair(String accessToken, LocalDateTime accessTokenExpiresAt, String refreshToken, LocalDateTime refreshTokenExpiresAt) {
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
    }

    public RefreshToken toRefreshToken(Member member) {
        return RefreshToken.create(member, refreshToken, refreshTokenExpiresAt);
    }
}
