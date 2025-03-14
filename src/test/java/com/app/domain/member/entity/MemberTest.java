package com.app.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static java.time.ZoneId.systemDefault;
import static org.assertj.core.api.Assertions.assertThat;

class MemberTest {

    @DisplayName("회원 생성 시 리프레시 토큰과 리프레시 토큰 만료 일시는 null이다.")
    @Test
    void buildMember() {
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
        Member member = createTestMember();

        Instant fixedFutureInstant = Instant.parse("2025-12-31T01:00:00Z");
        LocalDateTime issueDateTime = LocalDateTime.ofInstant(fixedFutureInstant, systemDefault());
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plusDays(14);

        // when
        member.updateRefreshToken("refresh-token", refreshTokenExpirationDateTime);

        // then
        assertThat(member.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(member.getRefreshTokenExpirationDateTime()).isEqualTo(refreshTokenExpirationDateTime);
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
}
