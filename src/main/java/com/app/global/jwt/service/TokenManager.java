package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import com.app.global.config.time.TimeConfig;
import com.app.global.error.ErrorType;
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
public class TokenManager {

    private static final String CLAIM_KEY_MEMBER_ID = "memberId";
    private static final String CLAIM_KEY_ROLE = "role";

    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final SecretKey secretKey;
    private final Clock jwtClock;

    public TokenManager(@Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds,
                        @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMilliseconds,
                        @Value("${jwt.base64-encoded-token-secret}") String tokenSecret,
                        Clock jwtClock
    ) {
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecret));
        this.jwtClock = jwtClock;
    }

    public TokenPair createTokenPair(Member member, Date issueDate) {
        String accessToken = createAccessToken(member, issueDate);
        LocalDateTime accessTokenExpirationDateTime = getExpirationDateTime(accessToken);

        String refreshToken = createRefreshToken(member, issueDate);
        LocalDateTime refreshTokenExpirationDateTime = getExpirationDateTime(refreshToken);

        return TokenPair.builder()
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(accessTokenExpirationDateTime)
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }

    public String createAccessToken(Member member, Date issueDate) {
        return createToken(member, issueDate, TokenType.ACCESS, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(Member member, Date issueDate) {
        return createToken(member, issueDate, TokenType.REFRESH, refreshTokenValidityInMilliseconds);
    }

    public void validateAccessToken(String accessToken) {
        validateToken(accessToken, TokenType.ACCESS);
    }

    public void validateRefreshToken(String refreshToken) {
        validateToken(refreshToken, TokenType.REFRESH);
    }

    public LocalDateTime getExpirationDateTime(String token) {
        Claims claims = getClaims(token);
        return claims.getExpiration()
                .toInstant()
                .atZone(TimeConfig.TIME_ZONE)
                .toLocalDateTime();
    }

    public MemberInfoDto getMemberInfo(String accessToken) {
        Claims claims = getClaims(accessToken);
        Long memberId = claims.get(CLAIM_KEY_MEMBER_ID, Long.class);
        String role = claims.get(CLAIM_KEY_ROLE, String.class);

        return MemberInfoDto.builder()
                .id(memberId)
                .role(Role.from(role))
                .build();
    }

    private String createToken(Member member, Date issueDate, TokenType tokenType, long validityInMilliseconds) {
        Date expirationDate = new Date(issueDate.getTime() + validityInMilliseconds);
        JwtBuilder jwtBuilder = Jwts.builder()
                .subject(tokenType.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim(CLAIM_KEY_MEMBER_ID, member.getId())
                .signWith(secretKey);

        if (tokenType == TokenType.ACCESS) {
            jwtBuilder.claim(CLAIM_KEY_ROLE, member.getRole());
        }
        return jwtBuilder.compact();
    }

    private void validateToken(String token, TokenType expectedTokenType) {
        Claims claims = getClaims(token);
        String tokenType = claims.getSubject();
        TokenType actualTokenType = TokenType.from(tokenType);

        if (actualTokenType.isDifferent(expectedTokenType)) {
            throw new AuthenticationException(ErrorType.INVALID_TOKEN_TYPE);
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
            throw new AuthenticationException(ErrorType.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AuthenticationException(ErrorType.INVALID_TOKEN);
        }
    }
}
