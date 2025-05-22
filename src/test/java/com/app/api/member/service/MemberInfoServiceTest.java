package com.app.api.member.service;

import com.app.api.member.service.dto.response.MemberInfoResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

class MemberInfoServiceTest extends IntegrationTestSupport {

    @Autowired
    MemberInfoService memberInfoService;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원 아이디로 회원 정보를 조회한다.")
    @Test
    void getMemberInfo() {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        // when
        MemberInfoResponse response = memberInfoService.getMemberInfo(member.getId());

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response)
                .extracting("name", "email", "profile", "role")
                .containsExactly("member", "member@email.com", "profile", USER.name());

        Optional<Member> optionalMember = memberRepository.findById(member.getId());
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("name", "email", "profile", "role")
                .containsExactly("member", "member@email.com", "profile", USER);
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
