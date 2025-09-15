package com.shopch.domain.token.service;

import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.domain.token.repository.RefreshTokenRepository;
import com.shopch.global.error.exception.AuthenticationException;
import com.shopch.global.error.exception.EntityNotFoundException;
import com.shopch.support.IntegrationTestSupport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.ONE_SECOND_IN_MILLIS;
import static com.shopch.fixture.TokenFixture.REFRESH_TOKEN_VALIDITY_MILLIS;
import static com.shopch.global.auth.constant.TokenType.REFRESH;
import static com.shopch.global.config.clock.ClockConfig.DEFAULT_TIME_ZONE;
import static com.shopch.global.error.ErrorCode.EXPIRED_REFRESH_TOKEN;
import static com.shopch.global.error.ErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.doReturn;

class RefreshTokenServiceTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String MEMBER_ID_NAME = "memberId";
    private static final int EXPECTED_SIZE = 1;
    private static final String NON_EXISTENT_TOKEN = "token";

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${jwt.token-secret}")
    private String tokenSecret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        doReturn(INSTANT_NOW).when(clock).instant();
        secretKey = Keys.hmacShaKeyFor(BASE64.decode(tokenSecret));
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("리프레시 토큰을 등록한다.")
    @Test
    void registerRefreshToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        String token = createToken(member, issuedAt);
        LocalDateTime expiresAt = calculateExpiresAt(issuedAt);

        RefreshToken refreshToken = createRefreshToken(member, token, expiresAt);

        // when
        refreshTokenService.registerRefreshToken(refreshToken);

        // then
        assertThat(refreshTokenRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        rt -> rt.getMember().getId(),
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                ).
                containsExactly(
                        tuple(
                                member.getId(),
                                token,
                                expiresAt
                        )
                );
    }

    @DisplayName("회원 아이디로 리프레시 토큰을 삭제한다.")
    @Test
    void deleteRefreshToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        String token = createToken(member, issuedAt);
        LocalDateTime expiresAt = calculateExpiresAt(issuedAt);

        RefreshToken refreshToken = createRefreshToken(member, token, expiresAt);
        refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenService.deleteRefreshToken(member.getId());

        // then
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    @DisplayName("회원 아이디로 리프레시 토큰을 조회한다.")
    @Test
    void findRefreshToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        String token = createToken(member, issuedAt);
        LocalDateTime expiresAt = calculateExpiresAt(issuedAt);

        RefreshToken refreshToken = createRefreshToken(member, token, expiresAt);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findRefreshToken(member.getId());

        // then
        assertThat(optionalRefreshToken).isPresent();
        assertThat(optionalRefreshToken.get())
                .extracting(
                        rt -> rt.getMember().getId(),
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                ).
                containsExactly(
                        member.getId(),
                        token,
                        expiresAt
                );
    }

    @DisplayName("토큰으로 리프레시 토큰을 조회한다.")
    @Test
    void getRefreshToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        String token = createToken(member, issuedAt);
        LocalDateTime expiresAt = calculateExpiresAt(issuedAt);

        RefreshToken refreshToken = createRefreshToken(member, token, expiresAt);
        refreshTokenRepository.save(refreshToken);

        // when
        RefreshToken foundRefreshToken = refreshTokenService.getRefreshToken(refreshToken.getToken());

        // then
        assertThat(foundRefreshToken)
                .extracting(
                        rt -> rt.getMember().getId(),
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                ).
                containsExactly(
                        member.getId(),
                        token,
                        expiresAt
                );
    }

    @DisplayName("등록된 토큰이 없을 때 조회를 시도할 경우, 예외가 발생한다.")
    @Test
    void getRefreshToken_RefreshTokenNotFound() {
        // when & then
        assertThatThrownBy(() -> refreshTokenService.getRefreshToken(NON_EXISTENT_TOKEN))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @DisplayName("주어진 토큰은 유효하나 등록된 토큰이 만료됐을 때 조회를 시도할 경우, 예외가 발생한다.")
    @Test
    void getRefreshToken_ExpiredStoredToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(REFRESH_TOKEN_VALIDITY_MILLIS);
        String token = createToken(member, issuedAt);
        LocalDateTime expiresAt = calculateExpiresAt(issuedAt.minusMillis(ONE_SECOND_IN_MILLIS));

        RefreshToken refreshToken = createRefreshToken(member, token, expiresAt);
        refreshTokenRepository.save(refreshToken);

        // when & then
        assertThatThrownBy(() -> refreshTokenService.getRefreshToken(token))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getMessage());
    }

    private Member createMember() {
        return Member.builder()
                .oauthId(OAUTH_ID)
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .role(USER)
                .oauthProvider(KAKAO)
                .build();
    }

    private String createToken(Member member, Instant issuedAt) {
        return Jwts.builder()
                .subject(REFRESH.name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(REFRESH_TOKEN_VALIDITY_MILLIS)))
                .claim(MEMBER_ID_NAME, member.getId())
                .signWith(secretKey)
                .compact();
    }

    private LocalDateTime calculateExpiresAt(Instant issuedAt) {
        return issuedAt.plusMillis(REFRESH_TOKEN_VALIDITY_MILLIS)
                .atZone(DEFAULT_TIME_ZONE)
                .toLocalDateTime();
    }

    private RefreshToken createRefreshToken(Member member, String token, LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .member(member)
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}
