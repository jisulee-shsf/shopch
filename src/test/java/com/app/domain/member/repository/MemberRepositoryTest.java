package com.app.domain.member.repository;

import com.app.domain.member.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("이메일로 회원을 조회한다.")
    @Test
    void findByEmail() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        // when
        Optional<Member> optionalMember = memberRepository.findByEmail(member.getEmail());

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
