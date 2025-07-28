package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
import com.app.global.error.ErrorType;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.GrantType;
import com.app.global.jwt.constant.TokenType;
import com.app.global.jwt.dto.TokenResponse;
import com.app.global.resolver.MemberInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.app.global.error.ErrorType.EXPIRED_TOKEN;
import static com.app.global.error.ErrorType.INVALID_TOKEN;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;

@RequiredArgsConstructor
public class TokenManager {

    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final SecretKey secretKey;
    private final Clock jwtClock;

    public TokenResponse createToken(Long memberId, Role role, Date issueDate) {
        Date accessTokenExpirationDate = createAccessTokenExpirationDate(issueDate);
        String accessToken = createAccessToken(memberId, role, issueDate, accessTokenExpirationDate);

        Date refreshTokenExpirationDate = createRefreshTokenExpirationDate(issueDate);
        String refreshToken = createRefreshToken(memberId, issueDate, refreshTokenExpirationDate);

        return TokenResponse.builder()
                .grantType(GrantType.BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(convertDateToLocalDateTime(accessTokenExpirationDate))
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(convertDateToLocalDateTime(refreshTokenExpirationDate))
                .build();
    }

    public Date createAccessTokenExpirationDate(Date issueDate) {
        return Date.from(issueDate.toInstant().plusMillis(accessTokenExpirationTime));
    }

    public String createAccessToken(Long memberId, Role role, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(TokenType.ACCESS.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .claim("role", role)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Date createRefreshTokenExpirationDate(Date issueDate) {
        return Date.from(issueDate.toInstant().plusMillis(refreshTokenExpirationTime));
    }

    public String createRefreshToken(Long memberId, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(TokenType.REFRESH.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .clock(jwtClock)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_TOKEN);
        }
    }

    public MemberInfoDto extractMemberInfo(String token) {
        Claims claims = extractClaims(token);
        return MemberInfoDto.builder()
                .id(claims.get("memberId", Long.class))
                .role(Role.from(claims.get("role", String.class)))
                .build();
    }

    public void validateAccessToken(String accessToken) {
        TokenType tokenType = extractTokenType(accessToken);
        validateTokenType(tokenType, TokenType.ACCESS);
    }

    public void validateRefreshToken(String refreshToken) {
        TokenType tokenType = extractTokenType(refreshToken);
        validateTokenType(tokenType, TokenType.REFRESH);
    }

    private TokenType extractTokenType(String token) {
        Claims claims = extractClaims(token);
        return TokenType.from(claims.getSubject());
    }

    private void validateTokenType(TokenType actualTokenType, TokenType expectedTokenType) {
        if (actualTokenType.isDifferent(expectedTokenType)) {
            throw new AuthenticationException(ErrorType.INVALID_TOKEN_TYPE);
        }
    }
}
