package com.shopch.api.member.service;

import com.shopch.api.member.service.dto.MemberInfoResponse;
import com.shopch.domain.member.constant.Role;
import com.shopch.domain.member.entity.Member;
import com.shopch.domain.member.repository.MemberRepository;
import com.shopch.domain.token.entity.RefreshToken;
import com.shopch.domain.token.repository.RefreshTokenRepository;
import com.shopch.support.IntegrationTestSupport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.ONE_SECOND_IN_MILLIS;
import static com.shopch.fixture.TokenFixture.REFRESH_TOKEN_VALIDITY_MILLIS;
import static com.shopch.global.auth.constant.TokenType.REFRESH;
import static com.shopch.global.config.clock.ClockConfig.DEFAULT_TIME_ZONE;
import static io.jsonwebtoken.io.Decoders.BASE64;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class MemberAccountServiceTest extends IntegrationTestSupport {

    private static final String OAUTH_ID = "1";
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String MEMBER_IMAGE_URL = "http://.../img_110x110.jpg";
    private static final String DELETED_AT_NAME = "deletedAt";
    private static final String MEMBER_ID_NAME = "memberId";

    @Autowired
    private MemberAccountService memberAccountService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.token-secret}")
    private String tokenSecret;

    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secretKey = Keys.hmacShaKeyFor(BASE64.decode(tokenSecret));
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원 정보를 조회한다.")
    @Test
    void getMemberInfo() {
        // given
        Member member = createMember(MEMBER_NAME, MEMBER_EMAIL, MEMBER_IMAGE_URL, USER);
        memberRepository.save(member);

        Long memberId = member.getId();

        // when
        MemberInfoResponse response = memberAccountService.getMemberInfo(memberId);

        // then
        assertThat(response)
                .extracting(
                        MemberInfoResponse::getId,
                        MemberInfoResponse::getName,
                        MemberInfoResponse::getEmail,
                        MemberInfoResponse::getImageUrl,
                        MemberInfoResponse::getRole
                )
                .containsExactly(
                        memberId,
                        MEMBER_NAME,
                        MEMBER_EMAIL,
                        MEMBER_IMAGE_URL,
                        USER.name()
                );
    }

    @DisplayName("리프레시 토큰을 삭제하고 삭제 일시를 등록해 회원을 논리적으로 삭제한다.")
    @Test
    @Transactional
    void deleteMember() {
        // given
        Member member = createMember(null);
        memberRepository.save(member);

        Instant issuedAt = INSTANT_NOW.minusMillis(ONE_SECOND_IN_MILLIS);
        RefreshToken refreshToken = createRefreshToken(member, issuedAt);
        refreshTokenRepository.save(refreshToken);

        LocalDateTime deletedAt = LocalDateTime.ofInstant(INSTANT_NOW, DEFAULT_TIME_ZONE);

        // when
        memberAccountService.deleteMember(member.getId(), deletedAt);

        // then
        assertThat(refreshTokenRepository.findAll()).isEmpty();
        assertThat(memberRepository.findAll()).isEmpty();
    }

    private Member createMember(String name, String email, String imageUrl, Role role) {
        return createMember(name, email, imageUrl, role, null);
    }

    private Member createMember(LocalDateTime deletedAt) {
        return createMember(MEMBER_NAME, MEMBER_EMAIL, MEMBER_IMAGE_URL, USER, deletedAt);
    }

    private Member createMember(String name, String email, String imageUrl, Role role, LocalDateTime deletedAt) {
        Member member = Member.builder()
                .oauthId(OAUTH_ID)
                .name(name)
                .email(email)
                .imageUrl(imageUrl)
                .role(role)
                .oauthProvider(KAKAO)
                .build();

        setField(member, DELETED_AT_NAME, deletedAt);
        return member;
    }

    private RefreshToken createRefreshToken(Member member, Instant issuedAt) {
        return RefreshToken.builder()
                .member(member)
                .token(createToken(member, issuedAt))
                .expiresAt(calculateExpiresAt(issuedAt))
                .build();
    }

    private String createToken(Member member, Instant issuedAt) {
        return Jwts.builder()
                .subject(REFRESH.name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusMillis(REFRESH_TOKEN_VALIDITY_MILLIS)))
                .claim(MEMBER_ID_NAME, member.getId())
                .signWith(secretKey)
                .compact();
    }

    private LocalDateTime calculateExpiresAt(Instant issuedAt) {
        return issuedAt.plusMillis(REFRESH_TOKEN_VALIDITY_MILLIS)
                .atZone(DEFAULT_TIME_ZONE)
                .toLocalDateTime();
    }
}
