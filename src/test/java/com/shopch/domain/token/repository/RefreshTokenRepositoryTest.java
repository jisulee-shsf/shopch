package com.shopch.domain.token.repository;

import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.TEST_TIME_ZONE;
import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRepositoryTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String TOKEN = "token";
    private static final LocalDateTime EXPIRES_AT = LocalDateTime.ofInstant(INSTANT_NOW, TEST_TIME_ZONE);

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원 아이디로 리프레시 토큰을 조회한다.")
    @Test
    void findByMember_Id() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        RefreshToken refreshToken = createRefreshToken(member, TOKEN, EXPIRES_AT);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMember_Id(member.getId());

        // then
        assertThat(optionalRefreshToken).isPresent();
        assertThat(optionalRefreshToken).get()
                .extracting(
                        rt -> rt.getMember().getId(),
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                ).
                containsExactly(
                        member.getId(),
                        TOKEN,
                        EXPIRES_AT
                );
    }

    @DisplayName("토큰으로 리프레시 토큰을 조회한다.")
    @Test
    void findByToken() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        RefreshToken refreshToken = createRefreshToken(member, TOKEN, EXPIRES_AT);
        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByToken(refreshToken.getToken());

        // then
        assertThat(optionalRefreshToken).isPresent();
        assertThat(optionalRefreshToken).get()
                .extracting(
                        rt -> rt.getMember().getId(),
                        RefreshToken::getToken,
                        RefreshToken::getExpiresAt
                ).
                containsExactly(
                        member.getId(),
                        TOKEN,
                        EXPIRES_AT
                );
    }

    @DisplayName("회원 아이디로 리프레시 토큰을 삭제한다.")
    @Test
    @Transactional
    void deleteByMember_Id() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        RefreshToken refreshToken = createRefreshToken(member, TOKEN, EXPIRES_AT);
        refreshTokenRepository.save(refreshToken);

        // when
        refreshTokenRepository.deleteByMember_Id(member.getId());

        // then
        assertThat(refreshTokenRepository.findAll()).isEmpty();
    }

    private Member createMember() {
        return Member.builder()
                .oauthId(OAUTH_ID)
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .role(USER)
                .oauthProvider(KAKAO)
                .build();
    }

    private RefreshToken createRefreshToken(Member member, String token, LocalDateTime expiresAt) {
        return RefreshToken.builder()
                .member(member)
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }
}
