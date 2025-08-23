package com.app.api.token.service;

import com.app.api.token.service.dto.request.RefreshAccessTokenServiceRequest;
import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.token.entity.RefreshToken;
import com.app.domain.token.service.RefreshTokenService;
import com.app.global.jwt.service.JwtProvider;
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
    private final JwtProvider jwtProvider;

    public AccessTokenResponse refreshAccessToken(RefreshAccessTokenServiceRequest request, Date issuedAt) {
        String refreshToken = request.getRefreshToken();
        jwtProvider.validateRefreshToken(refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshTokenByToken(refreshToken);
        Member member = refreshTokenEntity.getMember();

        String accessToken = jwtProvider.createAccessToken(member, issuedAt);
        LocalDateTime accessTokenExpiresAt = jwtProvider.getExpiresAt(accessToken);

        return AccessTokenResponse.of(accessToken, accessTokenExpiresAt);
    }
}
