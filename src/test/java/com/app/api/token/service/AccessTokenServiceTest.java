package com.app.api.token.service;

import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.error.exception.EntityNotFoundException;
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
import java.time.LocalDateTime;
import java.util.Date;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TokenFixture.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.app.fixture.TokenFixture.REFRESH_TOKEN_EXPIRATION_TIME;
import static com.app.global.error.ErrorType.EXPIRED_REFRESH_TOKEN;
import static com.app.global.error.ErrorType.MEMBER_NOT_FOUND;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;
import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64URL;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

class AccessTokenServiceTest extends IntegrationTestSupport {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${token.secret}")
    private String tokenSecret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        doReturn(FIXED_INSTANT).when(clock).instant();
        secretKey = Keys.hmacShaKeyFor(BASE64URL.decode(tokenSecret));
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("리프레시 토큰으로 액세스 토큰을 재발급한다.")
    @Test
    void createAccessTokenByRefreshToken() {
        // given
        Date issueDate = Date.from(FIXED_INSTANT);
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        Date reissueDate = Date.from(FIXED_INSTANT.plusMillis(1000));

        // when
        AccessTokenResponse response = accessTokenService.createAccessTokenByRefreshToken(member.getRefreshToken(), reissueDate);

        // then
        assertThat(response.getGrantType()).isEqualTo(BEARER.getType());

        assertThat(response.getAccessToken()).isNotNull();
        LocalDateTime reissueDateTime = convertDateToLocalDateTime(reissueDate);
        assertThat(response.getAccessTokenExpirationDateTime()).isEqualTo(reissueDateTime.plus(ACCESS_TOKEN_EXPIRATION_TIME, MILLIS));
    }

    @DisplayName("리프레시 토큰을 가진 회원이 없을 때 재발급을 시도할 경우, 예외가 발생한다.")
    @Test
    void createAccessTokenByRefreshToken_MemberNotFound() {
        // given
        Date issueDate = Date.from(FIXED_INSTANT);
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        String refreshToken = createTestRefreshToken(issueDate, refreshTokenExpirationDate);

        Date reissueDate = Date.from(FIXED_INSTANT.plusMillis(1000));

        // when & then
        assertThatThrownBy(() -> accessTokenService.createAccessTokenByRefreshToken(refreshToken, reissueDate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("만료된 리프레시 토큰으로 재발급을 시도할 경우, 예외가 발생한다.")
    @Test
    void createAccessTokenByRefreshToken_ExpiredRefreshToken() {
        // given
        Date issueDate = Date.from(FIXED_INSTANT.minusMillis(REFRESH_TOKEN_EXPIRATION_TIME + 1000));
        Date refreshTokenExpirationDate = Date.from(issueDate.toInstant().plusMillis(REFRESH_TOKEN_EXPIRATION_TIME));
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        String expiredRefreshToken = member.getRefreshToken();
        Date reissueDate = Date.from(FIXED_INSTANT.plusMillis(1000));

        // when & then
        assertThatThrownBy(() -> accessTokenService.createAccessTokenByRefreshToken(expiredRefreshToken, reissueDate))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getErrorMessage());
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
}
