package com.app.global.jwt.dto;

import com.app.domain.member.entity.Member;
import com.app.domain.token.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenPair {

    private final String accessToken;
    private final LocalDateTime accessTokenExpirationDateTime;
    private final String refreshToken;
    private final LocalDateTime refreshTokenExpirationDateTime;

    @Builder
    private TokenPair(String accessToken, LocalDateTime accessTokenExpirationDateTime, String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.accessToken = accessToken;
        this.accessTokenExpirationDateTime = accessTokenExpirationDateTime;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }

    public RefreshToken toEntity(Member member) {
        return RefreshToken.create(member, refreshToken, refreshTokenExpirationDateTime);
    }
}
