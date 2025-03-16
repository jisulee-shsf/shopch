package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.app.global.error.ErrorType.EXPIRED_TOKEN;
import static com.app.global.error.ErrorType.INVALID_TOKEN;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.jwt.constant.TokenType.ACCESS;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;
import static io.jsonwebtoken.Jwts.SIG.HS512;

@RequiredArgsConstructor
public class TokenManager {

    private final Long accessTokenExpirationDuration;
    private final Long refreshTokenExpirationDuration;
    private final SecretKey secretKey;

    public Date createAccessTokenExpirationDate(Date issueDate) {
        return new Date(issueDate.getTime() + accessTokenExpirationDuration);
    }

    public Date createRefreshTokenExpirationDate(Date issueDate) {
        return new Date(issueDate.getTime() + refreshTokenExpirationDuration);
    }

    public String createAccessToken(Long memberId, Role role, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(ACCESS.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .claim("role", role)
                .signWith(secretKey, HS512)
                .header()
                .add("typ", "JWT")
                .and()
                .compact();
    }

    public String createRefreshToken(Long memberId, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(REFRESH.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .signWith(secretKey, HS512)
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
                .grantType(BEARER.getType())
                .accessToken(accessToken)
                .accessTokenExpirationDateTime(convertDateToLocalDateTime(accessTokenExpirationDate))
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(convertDateToLocalDateTime(refreshTokenExpirationDate))
                .build();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_TOKEN);
        }
    }

    public Claims getTokenClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException(EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new AuthenticationException(INVALID_TOKEN);
        }
        return claims;
    }
}
