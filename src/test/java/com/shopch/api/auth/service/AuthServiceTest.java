package com.shopch.api.auth.service;

import com.shopch.api.auth.service.dto.request.OAuthLoginServiceRequest;
import com.shopch.api.auth.service.dto.request.RefreshAccessTokenServiceRequest;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
import com.shopch.api.auth.service.dto.response.OAuthLoginResponse;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.domain.token.repository.RefreshTokenRepository;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.external.oauth.provider.kakao.dto.request.KakaoTokenRequest;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoTokenResponse;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoUserInfoResponse;
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
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.*;
import static com.shopch.fixture.TokenFixture.*;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static com.shopch.global.auth.constant.TokenType.REFRESH;
import static com.shopch.global.config.clock.ClockConfig.DEFAULT_TIME_ZONE;
import static com.shopch.global.error.ErrorCode.*;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class AuthServiceTest extends IntegrationTestSupport {

    private static final String CODE = "code";
    private static final Integer EXPIRES_IN = 3600;
    private static final String SCOPE = "scope";
    private static final Long USER_ID = 1L;
    private static final String USER_EMAIL = "name@email.com";
    private static final String USER_NAME = "name";
    private static final String USER_IMAGE_URL = "http://.../img_110x110.jpg";
    private static final int EXPECTED_SIZE = 1;
    private static final String OAUTH_ID = "1";
    private static final String DELETED_AT_NAME = "deletedAt";
    private static final String MEMBER_ID_NAME = "memberId";

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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

    @DisplayName("신규 회원이 로그인할 경우, 등록 후 토큰을 발급하고 리프레시 토큰 정보를 등록한다.")
    @Test
    void oauthLogin_NewMember() {
        // given
        given(kakaoTokenClient.requestKakaoToken(any(KakaoTokenRequest.class)))
                .willReturn(createKakaoTokenResponse());

        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(createKakaoUserInfoResponse(USER_ID));

        OAuthLoginServiceRequest request = createOauthLoginServiceRequest(KAKAO, CODE);

        // when
        OAuthLoginResponse response = authService.oauthLogin(request, INSTANT_NOW);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();

        assertThat(memberRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        Member::getOauthId,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                )
                .containsExactly(
                        tuple(
                                String.valueOf(USER_ID),
                                KAKAO,
                                null
                        )
                );

        assertThat(refreshTokenRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                )
                .containsExactly(
                        tuple(
                                response.getRefreshToken(),
                                response.getRefreshTokenExpiresAt()
                        )
                );
    }

    @DisplayName("기존 회원이 로그인할 경우, 토큰을 발급하고 등록된 리프레시 토큰 정보를 변경한다.")
    @Test
    void oauthLogin_ActiveMember() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        RefreshToken refreshToken = createRefreshToken(member, issuedAt);
        refreshTokenRepository.save(refreshToken);

        given(kakaoTokenClient.requestKakaoToken(any(KakaoTokenRequest.class)))
                .willReturn(createKakaoTokenResponse());

        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(createKakaoUserInfoResponse(Long.valueOf(member.getOauthId())));

        OAuthLoginServiceRequest request = createOauthLoginServiceRequest(member.getOauthProvider(), CODE);

        // when
        OAuthLoginResponse response = authService.oauthLogin(request, INSTANT_NOW);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();

        assertThat(refreshTokenRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                )
                .containsExactly(
                        tuple(
                                response.getRefreshToken(),
                                response.getRefreshTokenExpiresAt()
                        )
                );
    }

    @DisplayName("탈퇴 회원이 재로그인할 경우, 등록 후 토큰을 발급하고 리프레시 토큰 정보를 등록한다.")
    @Test
    @Transactional
    void oauthLogin_InactiveMember() {
        // given
        LocalDateTime deletedAt = LocalDateTime.ofInstant(INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS), TEST_TIME_ZONE);
        Member member = createMember(OAUTH_ID, KAKAO, deletedAt);
        memberRepository.save(member);

        given(kakaoTokenClient.requestKakaoToken(any(KakaoTokenRequest.class)))
                .willReturn(createKakaoTokenResponse());

        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(createKakaoUserInfoResponse(Long.valueOf(member.getOauthId())));

        OAuthLoginServiceRequest request = createOauthLoginServiceRequest(member.getOauthProvider(), CODE);

        // when
        OAuthLoginResponse response = authService.oauthLogin(request, INSTANT_NOW);

        // when
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();

        assertThat(memberRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        Member::getOauthId,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                )
                .containsExactly(
                        tuple(
                                OAUTH_ID,
                                KAKAO,
                                null
                        )
                );

        assertThat(refreshTokenRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                )
                .containsExactly(
                        tuple(
                                response.getRefreshToken(),
                                response.getRefreshTokenExpiresAt()
                        )
                );
    }

    @DisplayName("리프레시 토큰으로 액세스 토큰을 갱신한다.")
    @Test
    void refreshAccessToken() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        RefreshToken refreshToken = createRefreshToken(member, issuedAt);
        refreshTokenRepository.save(refreshToken);

        RefreshAccessTokenServiceRequest request = new RefreshAccessTokenServiceRequest(refreshToken.getToken());

        // when
        AccessTokenResponse response = authService.refreshAccessToken(request, INSTANT_NOW);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getAccessTokenExpiresAt()).isNotNull();
    }

    @DisplayName("주어진 토큰은 유효하나 등록된 리프레시 토큰이 없을 때 액세스 토큰 갱신을 시도할 경우, 예외가 발생한다.")
    @Test
    void refreshAccessToken_RefreshTokenNotFound() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        String token = createToken(member, issuedAt);

        RefreshAccessTokenServiceRequest request = new RefreshAccessTokenServiceRequest(token);

        // when & then
        assertThatThrownBy(() -> authService.refreshAccessToken(request, INSTANT_NOW))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @DisplayName("주어진 리프레시 토큰이 만료됐을 때 액세스 토큰 갱신을 시도할 경우, 예외가 발생한다.")
    @Test
    void refreshAccessToken_ExpiredClientToken() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(REFRESH_TOKEN_VALIDITY_MILLIS + ONE_SECOND_IN_MILLIS);
        RefreshToken refreshToken = createRefreshToken(member, issuedAt);
        refreshTokenRepository.save(refreshToken);

        RefreshAccessTokenServiceRequest request = new RefreshAccessTokenServiceRequest(refreshToken.getToken());

        // when & then
        assertThatThrownBy(() -> authService.refreshAccessToken(request, INSTANT_NOW))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_TOKEN.getMessage());
    }

    @DisplayName("주어진 리프레시 토큰은 유효하나 등록된 리프레시 토큰이 만료됐을 때 액세스 토큰 갱신을 시도할 경우, 예외가 발생한다.")
    @Test
    void refreshAccessToken_ExpiredStoredToken() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(REFRESH_TOKEN_VALIDITY_MILLIS);
        String token = createToken(member, issuedAt);

        LocalDateTime expiresAt = calculateExpiresAt(issuedAt.minusMillis(ONE_SECOND_IN_MILLIS));
        RefreshToken refreshToken = createRefreshToken(member, token, expiresAt);
        refreshTokenRepository.save(refreshToken);

        RefreshAccessTokenServiceRequest request = new RefreshAccessTokenServiceRequest(token);

        // when & then
        assertThatThrownBy(() -> authService.refreshAccessToken(request, INSTANT_NOW))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @DisplayName("리프레시 토큰을 삭제해 로그아웃한다.")
    @Test
    void logout() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        RefreshToken refreshToken = createRefreshToken(member, issuedAt);
        refreshTokenRepository.save(refreshToken);

        // when
        authService.logout(member.getId());

        // then
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    private OAuthLoginServiceRequest createOauthLoginServiceRequest(OAuthProvider oauthProvider, String code) {
        return OAuthLoginServiceRequest.builder()
                .oauthProvider(oauthProvider)
                .code(code)
                .build();
    }

    private KakaoTokenResponse createKakaoTokenResponse() {
        return KakaoTokenResponse.builder()
                .tokenType(BEARER.getText())
                .accessToken(ACCESS_TOKEN)
                .expiresIn(EXPIRES_IN)
                .refreshToken(REFRESH_TOKEN)
                .refreshTokenExpiresIn(EXPIRES_IN)
                .scope(SCOPE)
                .build();
    }

    private KakaoUserInfoResponse createKakaoUserInfoResponse(Long userId) {
        return KakaoUserInfoResponse.builder()
                .id(userId)
                .kakaoAccount(KakaoUserInfoResponse.KakaoAccount.builder()
                        .email(USER_EMAIL)
                        .profile(KakaoUserInfoResponse.KakaoAccount.Profile.builder()
                                .nickname(USER_NAME)
                                .thumbnailImageUrl(USER_IMAGE_URL)
                                .build())
                        .build())
                .build();
    }

    private Member createMember(String oauthId, OAuthProvider oauthProvider, LocalDateTime deletedAt) {
        Member member = Member.builder()
                .oauthId(oauthId)
                .name(USER_NAME)
                .email(USER_EMAIL)
                .imageUrl(USER_IMAGE_URL)
                .role(USER)
                .oauthProvider(oauthProvider)
                .build();

        setField(member, DELETED_AT_NAME, deletedAt);
        return member;
    }

    private RefreshToken createRefreshToken(Member member, Instant issuedAt) {
        String token = createToken(member, issuedAt);
        LocalDateTime expiresAt = calculateExpiresAt(issuedAt);
        return createRefreshToken(member, token, expiresAt);
    }

    private RefreshToken createRefreshToken(Member member, String token, LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .member(member)
                .token(token)
                .expiresAt(expiresAt)
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
}
