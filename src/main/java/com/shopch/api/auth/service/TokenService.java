package com.shopch.api.auth.service;

import com.shopch.api.auth.service.dto.request.RefreshAccessTokenServiceRequest;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.domain.token.service.RefreshTokenService;
import com.shopch.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtProvider;

    public AccessTokenResponse refreshAccessToken(RefreshAccessTokenServiceRequest request, Date issuedAt) {
        String refreshToken = request.getRefreshToken();
        jwtProvider.validateRefreshToken(refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshToken(refreshToken);
        Member member = refreshTokenEntity.getMember();

        String accessToken = jwtProvider.createAccessToken(member, issuedAt);
        LocalDateTime accessTokenExpiresAt = jwtProvider.getExpiresAt(accessToken);

        return AccessTokenResponse.of(accessToken, accessTokenExpiresAt);
    }
}
