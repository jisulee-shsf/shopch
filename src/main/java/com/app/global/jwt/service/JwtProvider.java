package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import com.app.global.config.clock.ClockConfig;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.TokenType;
import com.app.global.jwt.dto.TokenPair;
import com.app.global.resolver.MemberInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String CLAIM_KEY_MEMBER_ID = "memberId";
    private static final String CLAIM_KEY_ROLE = "role";

    private final long accessTokenValidityMillis;
    private final long refreshTokenValidityMillis;
    private final SecretKey secretKey;
    private final Clock jwtClock;

    public JwtProvider(@Value("${jwt.access-token-validity}") long accessTokenValidityMillis,
                       @Value("${jwt.refresh-token-validity}") long refreshTokenValidityMillis,
                       @Value("${jwt.base64-encoded-token-secret}") String tokenSecret,
                       Clock jwtClock
    ) {
        this.accessTokenValidityMillis = accessTokenValidityMillis;
        this.refreshTokenValidityMillis = refreshTokenValidityMillis;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
        this.jwtClock = jwtClock;
    }

    public TokenPair createTokenPair(Member member, Date issuedAt) {
        String accessToken = createAccessToken(member, issuedAt);
        LocalDateTime accessTokenExpiresAt = getExpiresAt(accessToken);

        String refreshToken = createRefreshToken(member, issuedAt);
        LocalDateTime refreshTokenExpiresAt = getExpiresAt(refreshToken);

        return TokenPair.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .build();
    }

    public String createAccessToken(Member member, Date issuedAt) {
        return createToken(member, issuedAt, TokenType.ACCESS, accessTokenValidityMillis);
    }

    public String createRefreshToken(Member member, Date issuedAt) {
        return createToken(member, issuedAt, TokenType.REFRESH, refreshTokenValidityMillis);
    }

    public void validateAccessToken(String accessToken) {
        validateToken(accessToken, TokenType.ACCESS);
    }

    public void validateRefreshToken(String refreshToken) {
        validateToken(refreshToken, TokenType.REFRESH);
    }

    public LocalDateTime getExpiresAt(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration()
                .toInstant()
                .atZone(ClockConfig.TIME_ZONE)
                .toLocalDateTime();
    }

    public MemberInfoDto getMemberInfo(String accessToken) {
        Claims claims = getClaims(accessToken);
        TokenType tokenType = TokenType.from(claims.getSubject());
        validateTokenType(tokenType, TokenType.ACCESS);

        Long memberId = claims.get(CLAIM_KEY_MEMBER_ID, Long.class);
        String role = claims.get(CLAIM_KEY_ROLE, String.class);

        return MemberInfoDto.builder()
                .id(memberId)
                .role(Role.from(role))
                .build();
    }

    private String createToken(Member member, Date issuedAt, TokenType tokenType, long validityMillis) {
        Date expiration = new Date(issuedAt.getTime() + validityMillis);
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(tokenType.name())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim(CLAIM_KEY_MEMBER_ID, member.getId())
                .signWith(secretKey);

        if (tokenType == TokenType.ACCESS) {
            jwtBuilder.claim(CLAIM_KEY_ROLE, member.getRole());
        }
        return jwtBuilder.compact();
    }

    private void validateToken(String token, TokenType expectedTokenType) {
        Claims claims = getClaims(token);
        TokenType actualTokenType = TokenType.from(claims.getSubject());
        validateTokenType(actualTokenType, expectedTokenType);
    }

    public void validateTokenType(TokenType actualTokenType, TokenType expectedTokenType) {
        if (actualTokenType.isDifferent(expectedTokenType)) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN_TYPE);
        }
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .clock(jwtClock)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AuthenticationException(ErrorCode.INVALID_TOKEN);
        }
    }
}
