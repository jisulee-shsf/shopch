package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.constant.GrantType;
import com.app.global.jwt.constant.TokenType;
import com.app.global.jwt.dto.TokenResponse;
import com.app.global.util.DateTimeUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.app.global.error.ErrorType.EXPIRED_TOKEN;
import static com.app.global.error.ErrorType.INVALID_TOKEN;

@RequiredArgsConstructor
public class TokenManager {

    private final Long accessTokenExpirationTime;
    private final Long refreshTokenExpirationTime;
    private final SecretKey secretKey;
    private final Clock jwtClock;

    public Date createAccessTokenExpirationDate(Date issueDate) {
        return Date.from(issueDate.toInstant().plusMillis(accessTokenExpirationTime));
    }

    public Date createRefreshTokenExpirationDate(Date issueDate) {
        return Date.from(issueDate.toInstant().plusMillis(refreshTokenExpirationTime));
    }

    public String createAccessToken(Long memberId, Role role, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(TokenType.ACCESS.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .claim("role", role)
                .signWith(secretKey, Jwts.SIG.HS512)
                .header()
                .add("typ", "JWT")
                .and()
                .compact();
    }

    public String createRefreshToken(Long memberId, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(TokenType.REFRESH.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .signWith(secretKey, Jwts.SIG.HS512)
                .header()
                .add("typ", "JWT")
                .and()
                .compact();
    }

    public TokenResponse createToken(Long memberId, Role role, Date issueDate) {
        Date accessTokenExpirationDate = createAccessTokenExpirationDate(issueDate);
        Date refreshTokenExpirationDate = createRefreshTokenExpirationDate(issueDate);

        String accessToken = createAccessToken(memberId, role, issueDate, accessTokenExpirationDate);
        String refreshToken = createRefreshToken(memberId, issueDate, refreshTokenExpirationDate);

        return TokenResponse.builder()
                .grantType(GrantType.BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(DateTimeUtils.convertDateToLocalDateTime(accessTokenExpirationDate))
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(DateTimeUtils.convertDateToLocalDateTime(refreshTokenExpirationDate))
                .build();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().clock(jwtClock).verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_TOKEN);
        }
    }

    public Claims getTokenClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().clock(jwtClock).verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_TOKEN);
        }
        return claims;
    }
}
