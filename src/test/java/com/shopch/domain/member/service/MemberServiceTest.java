package com.shopch.domain.member.service;

import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.global.error.exception.EntityNotFoundException;
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
import static com.shopch.global.config.clock.ClockConfig.DEFAULT_TIME_ZONE;
import static com.shopch.global.error.ErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class MemberServiceTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String DELETED_AT_NAME = "deletedAt";
    public static final int EXPECTED_SIZE = 1;
    private static final Long NON_EXISTENT_MEMBER_ID = 1L;

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
        Member member = createMember(OAUTH_ID, KAKAO, null);

        // when
        Member registeredMember = memberService.registerMember(member);

        // then
        assertThat(registeredMember)
                .extracting(
                        Member::getOauthId,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                )
                .containsExactly(
                        OAUTH_ID,
                        KAKAO,
                        null
                );

        assertThat(memberRepository.findAll()).hasSize(EXPECTED_SIZE)
                .extracting(
                        Member::getOauthId,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                )
                .containsExactly(
                        tuple(
                                OAUTH_ID,
                                KAKAO,
                                null
                        )
                );
    }

    @DisplayName("활성 회원을 조회한다.")
    @Test
    @Transactional
    void findActiveMember() {
        // given
        Member member1 = createMember(OAUTH_ID, KAKAO, null);
        Member member2 = createMember(OAUTH_ID, KAKAO, LocalDateTime.ofInstant(INSTANT_NOW, DEFAULT_TIME_ZONE));
        memberRepository.saveAll(List.of(member1, member2));

        // when
        Optional<Member> optionalMember = memberService.findActiveMember(OAUTH_ID, KAKAO);

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

    @DisplayName("회원 아이디로 회원을 조회한다.")
    @Test
    void getMember() {
        // given
        Member member = createMember(OAUTH_ID, KAKAO, null);
        memberRepository.save(member);

        // when
        Member foundMember = memberService.getMember(member.getId());

        // then
        assertThat(foundMember)
                .extracting(
                        Member::getId,
                        Member::getOauthId,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                )
                .containsExactly(
                        member.getId(),
                        OAUTH_ID,
                        KAKAO,
                        null
                );
    }

    @DisplayName("등록된 회원이 없을 때 회원 조회를 시도할 경우, 예외가 발생한다.")
    @Test
    void getMember_MemberNotFound() {
        // when & then
        assertThatThrownBy(() -> memberService.getMember(NON_EXISTENT_MEMBER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
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
