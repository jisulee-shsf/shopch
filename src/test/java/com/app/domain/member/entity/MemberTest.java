package com.app.domain.member.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;

class MemberTest {

    @DisplayName("회원을 생성한다.")
    @Test
    void builder() {
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
        Assertions.assertThat(member)
                .extracting("name", "email", "role", "profile", "memberType")
                .containsExactly("member", "member@email.com", USER, "profile", KAKAO);
    }
}
