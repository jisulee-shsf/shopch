package com.app.api.token.service;

import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.global.jwt.constant.AuthenticationScheme;
import com.app.global.jwt.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {

    private final MemberService memberService;
    private final TokenManager tokenManager;

    public AccessTokenResponse createAccessTokenByRefreshToken(String refreshToken, Date issueDate) {
        tokenManager.validateRefreshToken(refreshToken);

        Member member = memberService.getMemberByRefreshToken(refreshToken);
        String accessToken = tokenManager.createAccessToken(member.getId(), member.getRole(), issueDate);
        LocalDateTime expirationDateTime = tokenManager.extractExpiration(accessToken);

        return AccessTokenResponse.builder()
                .authenticationScheme(AuthenticationScheme.BEARER.getText())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(expirationDateTime)
                .build();
    }
}
