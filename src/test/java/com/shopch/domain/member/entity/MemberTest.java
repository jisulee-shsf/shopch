package com.shopch.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.TEST_TIME_ZONE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class MemberTest {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String MEMBER_IMAGE_URL = "http://.../img_110x110.jpg";
    private static final String ID_NAME = "id";
    private static final Long MEMBER_1_ID = 1L;
    private static final Long MEMBER_2_ID = 2L;

    @DisplayName("회원 생성 시 삭제 일시는 null이다.")
    @Test
    void create() {
        // when
        Member member = Member.create(OAUTH_ID, MEMBER_NAME, MEMBER_EMAIL, MEMBER_IMAGE_URL, USER, KAKAO);

        // then
        assertThat(member)
                .extracting(
                        Member::getOauthId,
                        Member::getName,
                        Member::getEmail,
                        Member::getImageUrl,
                        Member::getRole,
                        Member::getOauthProvider,
                        Member::getDeletedAt
                ).
                containsExactly(
                        OAUTH_ID,
                        MEMBER_NAME,
                        MEMBER_EMAIL,
                        MEMBER_IMAGE_URL,
                        USER,
                        KAKAO,
                        null
                );
    }

    @DisplayName("삭제 일시를 주어진 삭제 일시로 변경한다.")
    @Test
    void updateDeletedAt() {
        // given
        Member member = createMember();
        LocalDateTime deletedAt = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);

        // when
        member.updateDeletedAt(deletedAt);

        // then
        assertThat(member.getDeletedAt()).isEqualTo(deletedAt);
    }

    @DisplayName("회원 아이디와 주어진 아이디가 같을 경우, true를 반환한다.")
    @Test
    void hasSameId_SameId() {
        // given
        Member member = createMember(MEMBER_1_ID);

        // when & then
        assertThat(member.hasSameId(MEMBER_1_ID)).isTrue();
    }

    @DisplayName("회원 아이디와 주어진 아이디가 다를 경우, false를 반환한다.")
    @Test
    void hasSameId_DifferentOrNullId() {
        // given
        Member member = createMember(MEMBER_1_ID);

        // when & then
        assertAll(
                () -> assertThat(member.hasSameId(MEMBER_2_ID)).isFalse(),
                () -> assertThat(member.hasSameId(null)).isFalse()
        );
    }

    private Member createMember() {
        return createMember(null);
    }

    private Member createMember(Long memberId) {
        Member member = Member.builder()
                .oauthId(OAUTH_ID)
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .role(USER)
                .imageUrl(MEMBER_IMAGE_URL)
                .oauthProvider(KAKAO)
                .build();

        setField(member, ID_NAME, memberId);
        return member;
    }
}
