package com.app.api.token.service;

import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.token.entity.RefreshToken;
import com.app.domain.token.service.RefreshTokenService;
import com.app.global.jwt.constant.AuthenticationScheme;
import com.app.global.jwt.service.TokenManager;
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
    private final TokenManager tokenManager;

    public AccessTokenResponse refreshAccessToken(String refreshToken, Date issueDate) {
        tokenManager.validateRefreshToken(refreshToken);

        RefreshToken refreshTokenEntity = refreshTokenService.getRefreshTokenByToken(refreshToken);
        Member member = refreshTokenEntity.getMember();

        String accessToken = tokenManager.createAccessToken(member.getId(), member.getRole(), issueDate);
        LocalDateTime accessTokenExpirationDateTime = tokenManager.extractExpirationDateTime(accessToken);

        return AccessTokenResponse.builder()
                .authenticationScheme(AuthenticationScheme.BEARER.getText())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(accessTokenExpirationDateTime)
                .build();
    }
}
