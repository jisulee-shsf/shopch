package com.app.global.jwt.service;

import com.app.domain.member.constant.Role;
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
import static com.app.global.error.ErrorType.EXPIRED_TOKEN;
import static com.app.global.error.ErrorType.INVALID_TOKEN;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.jwt.constant.TokenType.ACCESS;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64URL;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TokenManagerTest {

    private final Long accessTokenExpirationDuration = 900000L;
    private final Long refreshTokenExpirationDuration = 1209600000L;
    private final String testTokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(64));
    private final SecretKey testSecretKey = Keys.hmacShaKeyFor(BASE64URL.decode(testTokenSecret));
    private final TokenManager tokenManager = new TokenManager(accessTokenExpirationDuration, refreshTokenExpirationDuration, testSecretKey);

    @DisplayName("액세스 토큰을 발급한다.")
    @Test
    void createAccessToken() {
        // given
        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + accessTokenExpirationDuration);

        // when
        String accessToken = tokenManager.createAccessToken(1L, USER, issueDate, accessTokenExpirationDate);

        // then
        Claims claims = getTestTokenClaims(accessToken);
        assertThat(claims.getSubject()).isEqualTo(ACCESS.name());
//        assertThat(claims.getIssuedAt()).isEqualTo(issueDate); todo 이슈 생성 예정
//        assertThat(claims.getExpiration()).isEqualTo(accessTokenExpirationDate); todo 이슈 생성 예정
        assertThat(claims.get("memberId", Long.class)).isEqualTo(1L);
        assertThat(claims.get("role", String.class)).isEqualTo(USER.name());
    }

    @DisplayName("리프레시 토큰을 발급한다.")
    @Test
    void createRefreshToken() {
        // given
        Date issueDate = new Date();
        Date refreshTokenExpirationDate = new Date(issueDate.getTime() + refreshTokenExpirationDuration);

        // when
        String refreshToken = tokenManager.createRefreshToken(1L, issueDate, refreshTokenExpirationDate);

        // then
        Claims claims = getTestTokenClaims(refreshToken);
        assertThat(claims.getSubject()).isEqualTo(REFRESH.name());
//        assertThat(claims.getIssuedAt()).isEqualTo(issueDate); todo 이슈 생성 예정
//        assertThat(claims.getExpiration()).isEqualTo(refreshTokenExpirationDate); todo 이슈 생성 예정
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
//        assertThat(accessTokenClaims.getIssuedAt()).isEqualTo(issueDate); todo 이슈 생성 예정
//        assertThat(accessTokenClaims.getExpiration()).isEqualTo(new Date(issueDate.getTime() + accessTokenExpirationDuration)); todo 이슈 생성 예정
        assertThat(accessTokenClaims.get("memberId", Long.class)).isEqualTo(1L);
        assertThat(accessTokenClaims.get("role", String.class)).isEqualTo(USER.name());

        Claims refreshTokenClaims = getTestTokenClaims(response.getRefreshToken());
        assertThat(refreshTokenClaims.getSubject()).isEqualTo(REFRESH.name());
//        assertThat(refreshTokenClaims.getIssuedAt()).isEqualTo(issueDate); todo 이슈 생성 예정
//        assertThat(refreshTokenClaims.getExpiration()).isEqualTo(new Date(issueDate.getTime() +refreshTokenExpirationDuration)); todo 이슈 생성 예정
        assertThat(refreshTokenClaims.get("memberId", Long.class)).isEqualTo(1L);
    }

    @DisplayName("토큰을 검증한다.")
    @Test
    void validateToken() {
        // given
        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + accessTokenExpirationDuration);
        String accessToken = createTestAccessToken(1L, USER, issueDate, accessTokenExpirationDate);

        // when & then
        assertDoesNotThrow(() -> tokenManager.validateToken(accessToken));
    }

    @DisplayName("만료된 토큰의 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateToken_ExpiredToken() {
        // given
        Date issueDate = new Date();
        Date accessTokenExpirationDate = Date.from(issueDate.toInstant().minus(1, DAYS));
        String expiredAccessToken = createTestAccessToken(1L, USER, issueDate, accessTokenExpirationDate);

        // when & then
        assertThatThrownBy(() -> tokenManager.validateToken(expiredAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getErrorMessage());
    }

    @DisplayName("시크릿 키가 달라 유효하지 않은 토큰의 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateToken_InvalidToken() {
        // given
        String newTokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(128));
        SecretKey newSecretKey = Keys.hmacShaKeyFor(BASE64URL.decode(newTokenSecret));

        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + accessTokenExpirationDuration);
        String invalidAccessToken = createTestAccessToken(1L, USER, accessTokenExpirationDate, issueDate, newSecretKey);

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
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + accessTokenExpirationDuration);
        String accessToken = createTestAccessToken(1L, USER, issueDate, accessTokenExpirationDate);

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
        Date issueDate = new Date();
        Date accessTokenExpirationDate = Date.from(issueDate.toInstant().minus(1, DAYS));
        String expiredAccessToken = createTestAccessToken(1L, USER, issueDate, accessTokenExpirationDate);

        // when & then
        assertThatThrownBy(() -> tokenManager.getTokenClaims(expiredAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getErrorMessage());
    }

    @DisplayName("시크릿 키가 달라 유효하지 않은 토큰의 클레임 추출을 시도할 경우, 예외가 발생한다.")
    @Test
    void getTokenClaims_InvalidToken() {
        // given
        String newTokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(128));
        SecretKey newSecretKey = Keys.hmacShaKeyFor(BASE64URL.decode(newTokenSecret));

        Date issueDate = new Date();
        Date accessTokenExpirationDate = new Date(issueDate.getTime() + accessTokenExpirationDuration);
        String invalidAccessToken = createTestAccessToken(1L, USER, accessTokenExpirationDate, issueDate, newSecretKey);

        // when & then
        assertThatThrownBy(() -> tokenManager.getTokenClaims(invalidAccessToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN.getErrorMessage());
    }

    private String createTestAccessToken(Long memberId, Role role, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(ACCESS.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .claim("role", role)
                .signWith(testSecretKey, HS512)
                .header()
                .add("typ", "JWT")
                .and()
                .compact();
    }

    private String createTestAccessToken(Long memberId, Role role, Date issueDate, Date expirationDate, SecretKey secretKey) {
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

    private Claims getTestTokenClaims(String token) {
        return Jwts.parser().verifyWith(testSecretKey).build().parseSignedClaims(token).getPayload();
    }
}
