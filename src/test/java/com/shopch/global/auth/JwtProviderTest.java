package com.shopch.global.auth;

import com.shopch.domain.member.constant.Role;
import com.shopch.domain.member.entity.Member;
import com.shopch.global.auth.constant.TokenType;
import com.shopch.global.auth.dto.TokenPair;
import com.shopch.global.config.clock.JwtClock;
import com.shopch.global.error.exception.AuthenticationException;
import com.shopch.global.resolver.dto.MemberInfoDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.*;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN_VALIDITY_MILLIS;
import static com.shopch.fixture.TokenFixture.REFRESH_TOKEN_VALIDITY_MILLIS;
import static com.shopch.global.auth.constant.TokenType.ACCESS;
import static com.shopch.global.auth.constant.TokenType.REFRESH;
import static com.shopch.global.config.clock.ClockConfig.DEFAULT_TIME_ZONE;
import static com.shopch.global.error.ErrorCode.*;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class JwtProviderTest {

    private static final String TOKEN_SECRET = "7YWM7Iqk7Yq47Jqp7Jy866Gc7IOd7ISx7ZWY64qU7J207KeA7IiY7J2Y7Iuc7YGs66a/7YKk7J6F64uI64ukLg==";
    private static final String NEW_TOKEN_SECRET = "7YWM7Iqk7Yq47Jqp7Jy866Gc7IOd7ISx7ZWY64qU7J207KeA7IiY7J2Y7IOI66Gc7Jq07Iuc7YGs66a/7YKk7J6F64uI64ukLg==";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(BASE64.decode(TOKEN_SECRET));
    private static final io.jsonwebtoken.Clock JWT_CLOCK = new JwtClock(Clock.fixed(INSTANT_NOW, TEST_TIME_ZONE));
    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String ID_NAME = "id";
    private static final Long ID_VALUE = 1L;
    private static final String MEMBER_ID_NAME = "memberId";
    private static final String ROLE_NAME = "role";

    private final JwtProvider jwtProvider = new JwtProvider(
            ACCESS_TOKEN_VALIDITY_MILLIS,
            REFRESH_TOKEN_VALIDITY_MILLIS,
            TOKEN_SECRET,
            JWT_CLOCK);

    @DisplayName("액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void createTokenPair() {
        // given
        Member member = createMember();

        // when
        TokenPair tokenPair = jwtProvider.createTokenPair(member, INSTANT_NOW);

        // then
        Claims accessTokenClaims = getClaims(tokenPair.getAccessToken());
        assertThat(accessTokenClaims.getSubject()).isEqualTo(ACCESS.name());
        assertThat(tokenPair.getAccessTokenExpiresAt()).isEqualTo(getExpiresAt(INSTANT_NOW, ACCESS_TOKEN_VALIDITY_MILLIS));

        Claims refreshTokenClaims = getClaims(tokenPair.getRefreshToken());
        assertThat(refreshTokenClaims.getSubject()).isEqualTo(REFRESH.name());
        assertThat(tokenPair.getRefreshTokenExpiresAt()).isEqualTo(getExpiresAt(INSTANT_NOW, REFRESH_TOKEN_VALIDITY_MILLIS));
    }

    @DisplayName("액세스 토큰을 발급한다.")
    @Test
    void createAccessToken() {
        // given
        Member member = createMember();

        // when
        String accessToken = jwtProvider.createAccessToken(member, INSTANT_NOW);

        // then
        Claims claims = getClaims(accessToken);
        assertThat(claims.getSubject()).isEqualTo(ACCESS.name());
    }

    @DisplayName("리프레시 토큰을 발급한다.")
    @Test
    void createRefreshToken() {
        // given
        Member member = createMember();

        // when
        String refreshToken = jwtProvider.createRefreshToken(member, INSTANT_NOW);

        // then
        Claims claims = getClaims(refreshToken);
        assertThat(claims.getSubject()).isEqualTo(REFRESH.name());
    }

    @DisplayName("액세스 토큰을 검증한다.")
    @Test
    void validateAccessToken() {
        // given
        Member member = createMember();
        String accessToken = createAccessToken(member, INSTANT_NOW);

        // when & then
        assertDoesNotThrow(() -> jwtProvider.validateAccessToken(accessToken));
    }

    @DisplayName("리프레시 토큰을 검증한다.")
    @Test
    void validateRefreshToken() {
        // given
        Member member = createMember();
        String refreshToken = createRefreshToken(member, INSTANT_NOW);

        // when & then
        assertDoesNotThrow(() -> jwtProvider.validateRefreshToken(refreshToken));
    }

    @DisplayName("만료된 토큰의 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateAccessToken_ExpiredToken() {
        // given
        Member member = createMember();
        Instant issuedAt = INSTANT_NOW.minusMillis((ACCESS_TOKEN_VALIDITY_MILLIS + ONE_SECOND_IN_MILLIS));
        String expiredToken = createAccessToken(member, issuedAt);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateAccessToken(expiredToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getMessage());
    }

    @DisplayName("시크릿 키가 달라 유효하지 않은 토큰의 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateAccessToken_InvalidToken() {
        // given
        Member member = createMember();
        SecretKey newSecretKey = Keys.hmacShaKeyFor(BASE64.decode(NEW_TOKEN_SECRET));
        String invalidToken = createAccessToken(member, INSTANT_NOW, newSecretKey);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateAccessToken(invalidToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @DisplayName("토큰 타입과 다른 메서드로 토큰 검증을 시도할 경우, 예외가 발생한다.")
    @Test
    void validateAccessToken_InvalidTokenType() {
        // given
        Member member = createMember();
        String refreshToken = createRefreshToken(member, INSTANT_NOW);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateAccessToken(refreshToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN_TYPE.getMessage());
    }

    @DisplayName("토큰 클레임의 만료 일시를 반환한다.")
    @Test
    void getExpirationFrom() {
        // given
        Member member = createMember();
        String token = createAccessToken(member, INSTANT_NOW);

        // when
        LocalDateTime expiresAt = jwtProvider.getExpirationFrom(token);

        // then
        LocalDateTime expectedExpiresAt = getExpiresAt(INSTANT_NOW, ACCESS_TOKEN_VALIDITY_MILLIS);
        assertThat(expiresAt).isEqualTo(expectedExpiresAt);
    }

    @DisplayName("액세스 토큰 클레임의 회원 정보를 반환한다.")
    @Test
    void getMemberInfoFrom() {
        // given
        Member member = createMember(ID_VALUE, USER);
        String accessToken = createAccessToken(member, INSTANT_NOW);

        // when
        MemberInfoDto memberInfo = jwtProvider.getMemberInfoFrom(accessToken);

        // then
        assertThat(memberInfo.getId()).isEqualTo(ID_VALUE);
        assertThat(memberInfo.getRole()).isEqualTo(USER);
    }

    @DisplayName("리프레시 토큰으로 회원 정보 반환을 시도할 경우, 예외가 발생한다.")
    @Test
    void getMemberInfoFrom_InvalidTokenType() {
        // given
        Member member = createMember(ID_VALUE, USER);
        String refreshToken = createRefreshToken(member, INSTANT_NOW);

        // when & then
        assertThatThrownBy(() -> jwtProvider.getMemberInfoFrom(refreshToken))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN_TYPE.getMessage());
    }

    private Member createMember() {
        return createMember(ID_VALUE, USER);
    }

    private Member createMember(Long memberId, Role role) {
        Member member = Member.builder()
                .oauthId(OAUTH_ID)
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .role(role)
                .oauthProvider(KAKAO)
                .build();

        setField(member, ID_NAME, memberId);
        return member;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .clock(JWT_CLOCK)
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private LocalDateTime getExpiresAt(Instant issuedAt, long validityMillis) {
        return issuedAt.plusMillis(validityMillis)
                .atZone(DEFAULT_TIME_ZONE)
                .toLocalDateTime();
    }

    private String createAccessToken(Member member, Instant issuedAt) {
        return createToken(member, issuedAt, ACCESS, ACCESS_TOKEN_VALIDITY_MILLIS, SECRET_KEY);
    }

    private String createAccessToken(Member member, Instant issuedAt, SecretKey secretKey) {
        return createToken(member, issuedAt, ACCESS, ACCESS_TOKEN_VALIDITY_MILLIS, secretKey);
    }

    private String createRefreshToken(Member member, Instant issuedAt) {
        return createToken(member, issuedAt, REFRESH, REFRESH_TOKEN_VALIDITY_MILLIS, SECRET_KEY);
    }

    private String createToken(Member member, Instant issuedAt, TokenType tokenType, long validityMillis, SecretKey secretKey) {
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
}
