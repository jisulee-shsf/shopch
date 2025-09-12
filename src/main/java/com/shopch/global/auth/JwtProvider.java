package com.shopch.global.auth;

import com.shopch.domain.member.constant.Role;
import com.shopch.domain.member.entity.Member;
import com.shopch.global.auth.constant.TokenType;
import com.shopch.global.auth.dto.TokenPair;
import com.shopch.global.config.clock.ClockConfig;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthenticationException;
import com.shopch.global.resolver.dto.MemberInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtProvider {

    private static final String MEMBER_ID_NAME = "memberId";
    private static final String ROLE_NAME = "role";
    private final long accessTokenValidityMillis;
    private final long refreshTokenValidityMillis;
    private final SecretKey secretKey;
    private final Clock jwtClock;

    public JwtProvider(@Value("${jwt.access-token.validity-millis}") long accessTokenValidityMillis,
                       @Value("${jwt.refresh-token.validity-millis}") long refreshTokenValidityMillis,
                       @Value("${jwt.token-secret}") String tokenSecret,
                       Clock jwtClock
    ) {
        this.accessTokenValidityMillis = accessTokenValidityMillis;
        this.refreshTokenValidityMillis = refreshTokenValidityMillis;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
        this.jwtClock = jwtClock;
    }

    public TokenPair createTokenPair(Member member, Instant issuedAt) {
        String accessToken = createAccessToken(member, issuedAt);
        LocalDateTime accessTokenExpiresAt = getExpirationFrom(accessToken);

        String refreshToken = createRefreshToken(member, issuedAt);
        LocalDateTime refreshTokenExpiresAt = getExpirationFrom(refreshToken);

        return TokenPair.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .refreshToken(refreshToken)
                .refreshTokenExpiresAt(refreshTokenExpiresAt)
                .build();
    }

    public String createAccessToken(Member member, Instant issuedAt) {
        return createToken(member, issuedAt, TokenType.ACCESS, accessTokenValidityMillis);
    }

    public String createRefreshToken(Member member, Instant issuedAt) {
        return createToken(member, issuedAt, TokenType.REFRESH, refreshTokenValidityMillis);
    }

    public void validateAccessToken(String accessToken) {
        validateToken(accessToken, TokenType.ACCESS);
    }

    public void validateRefreshToken(String refreshToken) {
        validateToken(refreshToken, TokenType.REFRESH);
    }

    public LocalDateTime getExpirationFrom(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration()
                .toInstant()
                .atZone(ClockConfig.DEFAULT_TIME_ZONE)
                .toLocalDateTime();
    }

    public MemberInfoDto getMemberInfoFrom(String accessToken) {
        Claims claims = getClaims(accessToken);
        TokenType tokenType = TokenType.from(claims.getSubject());
        validateTokenType(tokenType, TokenType.ACCESS);

        Long memberId = claims.get(MEMBER_ID_NAME, Long.class);
        String role = claims.get(ROLE_NAME, String.class);

        return MemberInfoDto.builder()
                .id(memberId)
                .role(Role.from(role))
                .build();
    }

    private String createToken(Member member, Instant issuedAt, TokenType tokenType, long validityMillis) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(tokenType.name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(validityMillis)))
                .claim(MEMBER_ID_NAME, member.getId())
                .signWith(secretKey);

        if (tokenType.isAccess()) {
            jwtBuilder.claim(ROLE_NAME, member.getRole());
        }
        return jwtBuilder.compact();
    }

    private void validateToken(String token, TokenType expectedTokenType) {
        Claims claims = getClaims(token);
        TokenType actualTokenType = TokenType.from(claims.getSubject());
        validateTokenType(actualTokenType, expectedTokenType);
    }

    public void validateTokenType(TokenType actualTokenType, TokenType expectedTokenType) {
        if (expectedTokenType.isDifferent(actualTokenType)) {
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
