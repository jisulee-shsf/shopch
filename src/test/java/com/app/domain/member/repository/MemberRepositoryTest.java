package com.app.domain.member.repository;

import com.app.domain.member.entity.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.REFRESH_TOKEN_EXPIRATION_DURATION;
import static java.time.temporal.ChronoUnit.MILLIS;
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

        String email = member.getEmail();

        // when
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        // then
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("email")
                .isEqualTo(email);
    }

    @DisplayName("리프레시 토큰으로 회원을 조회한다.")
    @Test
    void findByRefreshToken() {
        // given
        LocalDateTime issueDateTime = LocalDateTime.of(2025, 1, 1, 1, 0);
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_DURATION, MILLIS);
        Member member = createTestMember("refresh-token", refreshTokenExpirationDateTime);
        memberRepository.save(member);

        String refreshToken = member.getRefreshToken();

        // when
        Optional<Member> optionalMember = memberRepository.findByRefreshToken(refreshToken);

        // then
        assertThat(optionalMember)
                .isPresent()
                .get()
                .extracting("refreshToken", "refreshTokenExpirationDateTime")
                .containsExactly(refreshToken, member.getRefreshTokenExpirationDateTime());
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
