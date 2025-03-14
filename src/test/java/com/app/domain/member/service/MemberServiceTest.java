package com.app.domain.member.service;

import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.global.error.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.global.error.ErrorType.ALREADY_REGISTERED_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원을 등록한다.")
    @Test
    void registerMember() {
        // given
        Member member = createTestMember("member@email.com");

        // when
        Long memberId = memberService.registerMember(member);

        // then
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("email")
                .isEqualTo(member.getEmail());
    }

    @DisplayName("이미 등록된 회원이 있을 때 회원과 같은 이메일로 등록을 시도할 경우, 예외가 발생한다.")
    @Test
    void registerMember_AlreadyRegisteredMember() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        Member duplicateMember = createTestMember("member@email.com");

        // when & then
        assertThatThrownBy(() -> memberService.registerMember(duplicateMember))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ALREADY_REGISTERED_MEMBER.getErrorMessage());
    }

    @DisplayName("이메일로 회원을 조회한다.")
    @Test
    void findMemberByEmail() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        // when
        Optional<Member> optionalMember = memberService.findMemberByEmail(member.getEmail());

        // then
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("email")
                .isEqualTo(member.getEmail());
    }

    private Member createTestMember(String email) {
        return Member.builder()
                .name("member")
                .email(email)
                .role(USER)
                .profile("profile")
                .memberType(KAKAO)
                .build();
    }
}
