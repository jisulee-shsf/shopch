package com.shopch.domain.member.repository;

import com.shopch.domain.member.entity.Member;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.TEST_TIME_ZONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class MemberRepositoryTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String DELETED_AT_NAME = "deletedAt";

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("OAuth 아이디와 OAuth 제공자 정보로 deletedAt이 null인 회원을 조회한다.")
    @Test
    @Transactional
    void findByOauthIdAndOauthProvider() {
        // given
        Member member1 = createMember(OAUTH_ID, KAKAO, null);
        Member member2 = createMember(OAUTH_ID, KAKAO, LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE));
        memberRepository.saveAll(List.of(member1, member2));

        // when
        Optional<Member> optionalMember = memberRepository.findByOauthIdAndOauthProvider(OAUTH_ID, KAKAO);

        // then
        assertThat(optionalMember).isPresent();
        assertThat(optionalMember).get()
                .extracting(
                        Member::getId,
                        Member::getOauthId,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                )
                .containsExactly(
                        member1.getId(),
                        OAUTH_ID,
                        KAKAO,
                        null
                );
    }

    private Member createMember(String oauthId, OAuthProvider oauthProvider, LocalDateTime deletedAt) {
        Member member = Member.builder()
                .oauthId(oauthId)
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .role(USER)
                .oauthProvider(oauthProvider)
                .build();

        setField(member, DELETED_AT_NAME, deletedAt);
        return member;
    }
}
