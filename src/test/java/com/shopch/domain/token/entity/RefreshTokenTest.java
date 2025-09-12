package com.shopch.domain.token.entity;

import com.shopch.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.*;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RefreshTokenTest {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String TOKEN_1 = "token1";
    private static final String TOKEN_2 = "token2";
    private static final LocalDateTime TOKEN_1_EXPIRES_AT = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);
    private static final LocalDateTime TOKEN_2_EXPIRES_AT = TOKEN_1_EXPIRES_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS);

    @DisplayName("리프레시 토큰을 생성한다.")
    @Test
    void create() {
        // given
        Member member = createMember();

        // when
        RefreshToken refreshToken = RefreshToken.create(member, TOKEN_1, TOKEN_1_EXPIRES_AT);

        // then
        assertThat(refreshToken)
                .extracting(
                        RefreshToken::getMember,
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt)
                .containsExactly(
                        member,
                        TOKEN_1,
                        TOKEN_1_EXPIRES_AT
                );
    }

    @DisplayName("리프레시 토큰 정보를 주어진 정보로 변경한다.")
    @Test
    void updateTokenInfo() {
        // given
        Member member = createMember();
        RefreshToken refreshToken = createRefreshToken(member, TOKEN_1, TOKEN_1_EXPIRES_AT);

        // when
        refreshToken.updateTokenInfo(TOKEN_2, TOKEN_2_EXPIRES_AT);

        // then
        assertThat(refreshToken.getToken()).isEqualTo(TOKEN_2);
        assertThat(refreshToken.getExpiresAt()).isEqualTo(TOKEN_2_EXPIRES_AT);
    }

    @DisplayName("토큰 만료 일시가 주어진 일시보다 이전일 경우, true를 반환한다.")
    @Test
    void isExpired_ExpiredToken() {
        // given
        Member member = createMember();
        RefreshToken refreshToken = createRefreshToken(member, TOKEN_1, TOKEN_1_EXPIRES_AT);

        // then
        assertThat(refreshToken.isExpired(TOKEN_1_EXPIRES_AT.plus(ONE_SECOND_IN_MILLIS, MILLIS))).isTrue();
    }

    @DisplayName("토큰 만료 일시가 주어진 일시보다 이후거나 같을 경우, false를 반환한다.")
    @Test
    void isExpired_ValidToken() {
        // given
        Member member = createMember();
        RefreshToken refreshToken = createRefreshToken(member, TOKEN_1, TOKEN_1_EXPIRES_AT);

        // then
        assertAll(
                () -> assertThat(refreshToken.isExpired(TOKEN_1_EXPIRES_AT.minus(ONE_SECOND_IN_MILLIS, MILLIS))).isFalse(),
                () -> assertThat(refreshToken.isExpired(TOKEN_1_EXPIRES_AT)).isFalse()
        );
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

    private RefreshToken createRefreshToken(Member member, String token, LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .member(member)
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}
