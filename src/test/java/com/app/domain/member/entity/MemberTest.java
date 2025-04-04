package com.app.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.REFRESH_TOKEN_EXPIRATION_DURATION;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @DisplayName("회원 생성 시 리프레시 토큰과 리프레시 토큰 만료 일시는 null이다.")
    @Test
    void createMember() {
        // given
        // when
        Member member = Member.builder()
                .name("member")
                .email("member@email.com")
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();

        // then
        assertThat(member.getRefreshToken()).isNull();
        assertThat(member.getRefreshTokenExpirationDateTime()).isNull();
    }

    @DisplayName("리프레시 토큰과 리프레시 토큰 만료 일시를 반영한다.")
    @Test
    void updateRefreshToken() {
        // given
        Member member = createTestMember(null, null);
        LocalDateTime issueDateTime = LocalDateTime.of(2025, 1, 1, 1, 0);
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_DURATION, MILLIS);

        // when
        member.updateRefreshToken("refresh-token", refreshTokenExpirationDateTime);

        // then
        assertThat(member.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(member.getRefreshTokenExpirationDateTime()).isEqualTo(refreshTokenExpirationDateTime);
    }

    @DisplayName("리프레시 토큰 만료 일시를 현재 일시로 변경해 만료한다.")
    @Test
    void expireRefreshToken() {
        // given
        LocalDateTime issueDateTime = LocalDateTime.of(2025, 1, 1, 1, 0);
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_DURATION, MILLIS);
        Member member = createTestMember("refresh-token", refreshTokenExpirationDateTime);

        LocalDateTime now = issueDateTime.plusDays(1);

        // when
        member.expireRefreshToken(now);

        // then
        assertThat(member.getRefreshTokenExpirationDateTime()).isEqualTo(now);
    }

    private Member createTestMember(String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        return Member.builder()
                .name("member")
                .email("member@email.com")
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .refreshToken(refreshToken)
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();
    }
}
