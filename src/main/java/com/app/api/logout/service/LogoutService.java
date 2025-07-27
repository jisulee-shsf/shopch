package com.app.api.logout.service;

import com.app.domain.member.entity.Member;
import com.app.domain.member.service.MemberService;
import com.app.global.jwt.service.TokenManager;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class LogoutService {

    private final TokenManager tokenManager;
    private final MemberService memberService;

    public void logout(String accessToken, LocalDateTime now) {
        tokenManager.validateToken(accessToken);

        Claims claims = tokenManager.getTokenClaims(accessToken);
        String tokenType = claims.getSubject();
        validateTokenType(tokenType);

        Long memberId = claims.get("memberId", Long.class);
        Member member = memberService.getMemberById(memberId);
        member.expireRefreshToken(now);
    }

    private void validateTokenType(String tokenType) {
        if (!TokenType.isAccessToken(tokenType)) {
            throw new AuthenticationException(INVALID_TOKEN_TYPE);
        }
    }
}
