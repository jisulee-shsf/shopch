package com.app.global.jwt.service;

import com.app.global.error.exception.AuthenticationException;
import com.app.global.jwt.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.*;
import static com.app.global.error.ErrorType.EXPIRED_TOKEN;
import static com.app.global.error.ErrorType.INVALID_TOKEN;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.jwt.constant.TokenType.ACCESS;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TokenManagerTest {

    private final String tokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(64));
    private final SecretKey secretKey = Keys.hmacShaKeyFor(BASE64URL.decode(tokenSecret));
    private final TokenManager tokenManager = new TokenManager(ACCESS_TOKEN_EXPIRATION_DURATION, REFRESH_TOKEN_EXPIRATION_DURATION, secretKey);

    @DisplayName("액세스 토큰을 발급한다.")
    @Test
    void createAccessToken() {
        // given
        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);

        // when
        String accessToken = tokenManager.createAccessToken(1L, USER, issueDate, accessTokenExpirationDate);

        // then
        Claims claims = getTestTokenClaims(accessToken);
        assertThat(claims.getSubject()).isEqualTo(ACCESS.name());
        assertThat(claims.getIssuedAt()).isEqualTo(roundOffMillis(issueDate));
        assertThat(claims.getExpiration()).isEqualTo(roundOffMillis(accessTokenExpirationDate));
        assertThat(claims.get("memberId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo(USER.name());
    }

    @DisplayName("리프레시 토큰을 발급한다.")
    @Test
    void createRefreshToken() {
        // given
        Date issueDate = new Date();
        Date refreshTokenExpirationDate = new Date(issueDate.getTime() + REFRESH_TOKEN_EXPIRATION_DURATION);

        // when
        String refreshToken = tokenManager.createRefreshToken(1L, issueDate, refreshTokenExpirationDate);

        // then
        Claims claims = getTestTokenClaims(refreshToken);
        assertThat(claims.getSubject()).isEqualTo(REFRESH.name());
        assertThat(claims.getIssuedAt()).isEqualTo(roundOffMillis(issueDate));
        assertThat(claims.getExpiration()).isEqualTo(roundOffMillis(refreshTokenExpirationDate));
        assertThat(claims.get("memberId", Long.class)).isEqualTo(1L);
    }

    @DisplayName("액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void createToken() {
        // given
        Date issueDate = new Date();

        // when
        TokenResponse response = tokenManager.createToken(1L, USER, issueDate);

        // then
        assertThat(response.getGrantType()).isEqualTo(BEARER.getType());

        Claims accessTokenClaims = getTestTokenClaims(response.getAccessToken());
        assertThat(accessTokenClaims.getSubject()).isEqualTo(ACCESS.name());
        assertThat(accessTokenClaims.getIssuedAt()).isEqualTo(roundOffMillis(issueDate));
        assertThat(accessTokenClaims.getExpiration()).isEqualTo(
                roundOffMillis(new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION)));
        assertThat(accessTokenClaims.get("memberId", Long.class)).isEqualTo(1L);
        assertThat(accessTokenClaims.get("role", String.class)).isEqualTo(USER.name());

        Claims refreshTokenClaims = getTestTokenClaims(response.getRefreshToken());
        assertThat(refreshTokenClaims.getSubject()).isEqualTo(REFRESH.name());
        assertThat(refreshTokenClaims.getIssuedAt()).isEqualTo(roundOffMillis(issueDate));
        assertThat(refreshTokenClaims.getExpiration()).isEqualTo(
                roundOffMillis(new Date(issueDate.getTime() + REFRESH_TOKEN_EXPIRATION_DURATION)));
        assertThat(refreshTokenClaims.get("memberId", Long.class)).isEqualTo(1L);
    }

    @DisplayName("토큰을 검증한다.")
    @Test
    void validateToken() {
        // given
        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);
        String accessToken = createTestAccessToken(issueDate, accessTokenExpirationDate);

        // when & then
        assertDoesNotThrow(() -> tokenManager.validateToken(accessToken));
    }

    @DisplayName("만료된 토큰의 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateToken_ExpiredToken() {
        // given
        Date issueDate = Date.from(FIXED_PAST_INSTANT);
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);
        String expiredAccessToken = createTestAccessToken(issueDate, accessTokenExpirationDate);

        // when & then
        assertThatThrownBy(() -> tokenManager.validateToken(expiredAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getErrorMessage());
    }

    @DisplayName("시크릿 키가 달라 유효하지 않은 토큰의 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateToken_InvalidToken() {
        // given
        String newTokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(64));
        SecretKey newSecretKey = Keys.hmacShaKeyFor(BASE64URL.decode(newTokenSecret));

        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);
        String invalidAccessToken = createTestAccessToken(accessTokenExpirationDate, issueDate, newSecretKey);

        // when & then
        assertThatThrownBy(() -> tokenManager.validateToken(invalidAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN.getErrorMessage());
    }

    @DisplayName("토큰에서 클레임을 추출한다.")
    @Test
    void getTokenClaims() {
        // given
        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);
        String accessToken = createTestAccessToken(issueDate, accessTokenExpirationDate);

        // when
        Claims claims = tokenManager.getTokenClaims(accessToken);

        // then
        assertThat(claims.get("memberId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo(USER.name());
    }

    @DisplayName("만료된 토큰의 클레임 추출을 시도할 경우, 예외가 발생한다.")
    @Test
    void getTokenClaims_ExpiredToken() {
        // given
        Date issueDate = Date.from(FIXED_PAST_INSTANT);
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);
        String expiredAccessToken = createTestAccessToken(issueDate, accessTokenExpirationDate);

        // when & then
        assertThatThrownBy(() -> tokenManager.getTokenClaims(expiredAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getErrorMessage());
    }

    @DisplayName("시크릿 키가 달라 유효하지 않은 토큰의 클레임 추출을 시도할 경우, 예외가 발생한다.")
    @Test
    void getTokenClaims_InvalidToken() {
        // given
        String newTokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(64));
        SecretKey newSecretKey = Keys.hmacShaKeyFor(BASE64URL.decode(newTokenSecret));

        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + ACCESS_TOKEN_EXPIRATION_DURATION);
        String invalidAccessToken = createTestAccessToken(accessTokenExpirationDate, issueDate, newSecretKey);

        // when & then
        assertThatThrownBy(() -> tokenManager.getTokenClaims(invalidAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN.getErrorMessage());
    }

    private String createTestAccessToken(Date issueDate, Date expirationDate) {
        return createTestAccessToken(issueDate, expirationDate, secretKey);
    }

    private String createTestAccessToken(Date issueDate, Date expirationDate, SecretKey secretKey) {
        return Jwts.builder()
                .subject(ACCESS.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", 1L)
                .claim("role", USER)
                .signWith(secretKey, HS512)
                .compact();
    }

    private Claims getTestTokenClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    private Date roundOffMillis(Date date) {
        return new Date(date.getTime() / 1000 * 1000);
    }
}
