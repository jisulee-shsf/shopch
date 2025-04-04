package com.app.api.token.service;

import com.app.api.token.dto.AccessTokenResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.global.error.exception.AuthenticationException;
import com.app.global.error.exception.EntityNotFoundException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.*;
import static com.app.global.error.ErrorType.EXPIRED_REFRESH_TOKEN;
import static com.app.global.error.ErrorType.MEMBER_NOT_FOUND;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.jwt.constant.TokenType.REFRESH;
import static io.jsonwebtoken.Jwts.SIG.HS512;
import static io.jsonwebtoken.io.Decoders.BASE64URL;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AccessTokenServiceTest {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private MemberRepository memberRepository;

    @Value("${token.secret}")
    private String tokenSecret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
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
        Date issueDate = new Date();
        Date refreshTokenExpirationDate = new Date(issueDate.getTime() + REFRESH_TOKEN_EXPIRATION_DURATION);
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        Date reissueDate = Date.from(issueDate.toInstant().plus(1, DAYS));

        // when
        AccessTokenResponse response = accessTokenService.createAccessTokenByRefreshToken(member.getRefreshToken(), reissueDate);

        // then
        assertThat(response.getGrantType()).isEqualTo(BEARER.getType());

        assertThat(response.getAccessToken()).isNotNull();
        LocalDateTime reissueDateTime = LocalDateTime.ofInstant(reissueDate.toInstant(), systemDefault());
        assertThat(response.getAccessTokenExpirationDateTime()).isEqualTo(reissueDateTime.plus(ACCESS_TOKEN_EXPIRATION_DURATION, MILLIS));
    }

    @DisplayName("리프레시 토큰을 가진 회원이 없을 때 재발급을 시도할 경우, 예외가 발생한다.")
    @Test
    void createAccessTokenByRefreshToken_MemberDoesNotExist() {
        // given
        Date issueDate = new Date();
        Date refreshTokenExpirationDate = new Date(issueDate.getTime() + REFRESH_TOKEN_EXPIRATION_DURATION);
        String refreshToken = createTestRefreshToken(1L, issueDate, refreshTokenExpirationDate);

        Date reissueDate = Date.from(issueDate.toInstant().plus(1, DAYS));

        // when & then
        assertThatThrownBy(() -> accessTokenService.createAccessTokenByRefreshToken(refreshToken, reissueDate))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getErrorMessage());
    }

    @DisplayName("만료된 리프레시 토큰으로 재발급을 시도할 경우, 예외가 발생한다.")
    @Test
    void createAccessTokenByRefreshToken_ExpiredRefreshToken() {
        // given
        Date issueDate = Date.from(FIXED_PAST_INSTANT);
        Date refreshTokenExpirationDate = new Date(issueDate.getTime() + REFRESH_TOKEN_EXPIRATION_DURATION);
        Member member = createTestMemberWithRefreshToken(issueDate, refreshTokenExpirationDate);
        memberRepository.save(member);

        String expiredRefreshToken = member.getRefreshToken();

        Date reissueDate = Date.from(issueDate.toInstant().plus(1, DAYS));

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

    private String createTestRefreshToken(Long memberId, Date issueDate, Date expirationDate) {
        return Jwts.builder()
                .subject(REFRESH.name())
                .issuedAt(issueDate)
                .expiration(expirationDate)
                .claim("memberId", memberId)
                .signWith(secretKey, HS512)
                .compact();
    }

    private Member createTestMemberWithRefreshToken(Date issueDate, Date refreshTokenExpirationDate) {
        Member member = createTestMember();
        String refreshToken = createTestRefreshToken(member.getId(), issueDate, refreshTokenExpirationDate);
        LocalDateTime refreshTokenExpirationDateTime = LocalDateTime.ofInstant(refreshTokenExpirationDate.toInstant(), systemDefault());

        return member.toBuilder()
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }
}
