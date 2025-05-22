package com.app.api.logout.service;

import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.global.error.exception.AuthenticationException;
import com.app.support.IntegrationTestSupport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.FIXED_CLOCK;
import static com.app.fixture.TokenFixture.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.app.fixture.TokenFixture.REFRESH_TOKEN_EXPIRATION_TIME;
import static com.app.global.error.ErrorType.*;
import static com.app.global.jwt.constant.TokenType.ACCESS;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;
import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.doReturn;

class LogoutServiceTest extends IntegrationTestSupport {

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${token.secret}")
    private String tokenSecret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        doReturn(Date.from(FIXED_CLOCK.instant())).when(jwtClock).now();
        secretKey = Keys.hmacShaKeyFor(BASE64URL.decode(tokenSecret));
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("리프레시 토큰 만료 일시를 현재 일시로 변경해 회원을 로그아웃한다.")
    @Test
    void logout() {
        // given
        Date issueDate = jwtClock.now();
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        Date accessTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(ACCESS_TOKEN_EXPIRATION_TIME));
        String accessToken = createTestAccessToken(member.getId(), issueDate, accessTokenExpirationDate);

        LocalDateTime now = convertDateToLocalDateTime(jwtClock.now());

        // when
        logoutService.logout(accessToken, now);

        // then
        Optional<Member> optionalMember = memberRepository.findById(member.getId());
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("refreshTokenExpirationDateTime")
                .isEqualTo(now);
    }

    @DisplayName("만료된 액세스 토큰으로 로그아웃을 시도할 경우, 예외가 발생한다.")
    @Test
    void logout_ExpiredAccessToken() {
        // given
        Date issueDate = Date.from(jwtClock.now().toInstant().minusMillis(ACCESS_TOKEN_EXPIRATION_TIME + 1000));
        Date accessTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(ACCESS_TOKEN_EXPIRATION_TIME));
        String expiredAccessToken = createTestAccessToken(1L, issueDate, accessTokenExpirationDate);

        LocalDateTime now = convertDateToLocalDateTime(jwtClock.now());

        // when & then
        assertThatThrownBy(() -> logoutService.logout(expiredAccessToken, now))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getErrorMessage());
    }

    @DisplayName("시크릿 키가 달라 유효하지 않은 액세스 토큰으로 로그아웃을 시도할 경우, 예외가 발생한다.")
    @Test
    void logout_InvalidAccessToken() {
        // given
        Date issueDate = jwtClock.now();
        Date accessTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(ACCESS_TOKEN_EXPIRATION_TIME));

        String newTokenSecret = Base64.getUrlEncoder().encodeToString(new SecureRandom().generateSeed(64));
        SecretKey newSecretKey = Keys.hmacShaKeyFor(BASE64URL.decode(newTokenSecret));
        String invalidAccessToken = createTestAccessToken(1L, issueDate, accessTokenExpirationDate, newSecretKey);

        LocalDateTime now = convertDateToLocalDateTime(jwtClock.now());

        // when & then
        assertThatThrownBy(() -> logoutService.logout(invalidAccessToken, now))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN.getErrorMessage());
    }

    @DisplayName("액세스 타입이 아닌 토큰으로 로그아웃을 시도할 경우, 예외가 발생한다.")
    @Test
    void logout_InvalidTokenType() {
        Date issueDate = jwtClock.now();
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        String refreshToken = createTestRefreshToken(issueDate, refreshTokenExpirationDate);

        LocalDateTime now = convertDateToLocalDateTime(jwtClock.now());

        // when & then
        assertThatThrownBy(() -> logoutService.logout(refreshToken, now))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(INVALID_TOKEN_TYPE.getErrorMessage());
    }

    private Member createTestMember() {
        return Member.builder()
                .name("member")
                .email("member@email.com")
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();
    }

    private String createTestRefreshToken(Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(REFRESH.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .signWith(secretKey, HS512)
                .compact();
    }

    private Member createTestMemberWithRefreshToken(Date issueDate, Date refreshTokenExpirationDate) {
        Member member = createTestMember();
        String refreshToken = createTestRefreshToken(issueDate, refreshTokenExpirationDate);
        LocalDateTime refreshTokenExpirationDateTime = convertDateToLocalDateTime(refreshTokenExpirationDate);

        return member.toBuilder()
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }

    private String createTestAccessToken(Long memberId, Date issueDate, Date expirationDate) {
        return createTestAccessToken(memberId, issueDate, expirationDate, secretKey);
    }

    private String createTestAccessToken(Long memberId, Date issueDate, Date expirationDate, SecretKey secretKey) {
        return Jwts.builder()
                .subject(ACCESS.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .claim("role", USER)
                .signWith(secretKey, HS512)
                .compact();
    }
}
