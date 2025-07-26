package com.app.api.login.service;

import com.app.api.login.service.dto.request.OauthLoginServiceRequest;
import com.app.api.login.service.dto.response.OauthLoginResponse;
import com.app.domain.member.entity.Member;
import com.app.domain.member.repository.MemberRepository;
import com.app.external.oauth.kakao.dto.response.KakaoUserInfoResponse;
import com.app.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Date;

import static com.app.domain.member.constant.MemberType.KAKAO;
import static com.app.domain.member.constant.Role.USER;
import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TokenFixture.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.app.fixture.TokenFixture.REFRESH_TOKEN_EXPIRATION_TIME;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.util.DateTimeUtils.convertDateToLocalDateTime;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class OauthLoginServiceTest extends IntegrationTestSupport {

    @Autowired
    private OauthLoginService oauthLoginService;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("카카오로 로그인한 기존 회원의 액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void oauthLogin_AlreadyRegisteredMember() {
        // given
        Member member = createTestMember("member@email.com");
        memberRepository.save(member);

        OauthLoginServiceRequest request = new OauthLoginServiceRequest(KAKAO);

        KakaoUserInfoResponse userInfoResponse = createTestKakaoInfoResponse(member.getEmail());
        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(userInfoResponse);

        Date issueDate = Date.from(FIXED_INSTANT);

        // when
        OauthLoginResponse response = oauthLoginService.oauthLogin(request, BEARER.getType() + " access-token", issueDate);

        // then
        assertThat(response.getGrantType()).isEqualTo(BEARER.getType());

        assertThat(response.getAccessToken()).isNotNull();
        LocalDateTime issueDateTime = convertDateToLocalDateTime(issueDate);
        assertThat(response.getAccessTokenExpirationDateTime()).isEqualTo(issueDateTime.plus(ACCESS_TOKEN_EXPIRATION_TIME, MILLIS));

        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getRefreshTokenExpirationDateTime()).isEqualTo(issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_TIME, MILLIS));
    }

    @DisplayName("카카오로 로그인한 신규 회원의 액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void oauthLogin_NotRegisteredMember() {
        // given
        OauthLoginServiceRequest request = new OauthLoginServiceRequest(KAKAO);

        KakaoUserInfoResponse userInfoResponse = createTestKakaoInfoResponse("member@email.com");
        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(userInfoResponse);

        Date issueDate = Date.from(FIXED_INSTANT);

        // when
        OauthLoginResponse response = oauthLoginService.oauthLogin(request, BEARER.getType() + " access-token", issueDate);

        // then
        assertThat(response.getGrantType()).isEqualTo(BEARER.getType());

        assertThat(response.getAccessToken()).isNotNull();
        LocalDateTime issueDateTime = convertDateToLocalDateTime(issueDate);
        assertThat(response.getAccessTokenExpirationDateTime()).isEqualTo(issueDateTime.plus(ACCESS_TOKEN_EXPIRATION_TIME, MILLIS));

        assertThat(response.getRefreshToken()).isNotNull();
        assertThat(response.getRefreshTokenExpirationDateTime()).isEqualTo(issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_TIME, MILLIS));
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

    private KakaoUserInfoResponse createTestKakaoInfoResponse(String email) {
        return KakaoUserInfoResponse.builder()
                .id(1L)
                .kakaoAccount(KakaoUserInfoResponse.KakaoAccount.builder()
                        .email(email)
                        .profile(KakaoUserInfoResponse.KakaoAccount.Profile.builder()
                                .nickname("member")
                                .thumbnailImageUrl("http://img1.kakaocdn.net/.../thumbnail.jpeg")
                                .build())
                        .build())
                .build();
    }
}
