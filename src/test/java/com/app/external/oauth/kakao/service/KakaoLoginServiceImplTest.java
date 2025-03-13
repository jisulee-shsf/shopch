package com.app.external.oauth.kakao.service;

import com.app.external.oauth.dto.SocialLoginUserInfoResponse;
import com.app.external.oauth.kakao.client.KakaoUserInfoClient;
import com.app.external.oauth.kakao.dto.KakaoUserInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceImplTest {

    @Mock
    private KakaoUserInfoClient kakaoUserInfoClient;

    @InjectMocks
    private KakaoLoginServiceImpl kakaoLoginServiceImpl;

    @DisplayName("카카오 토큰으로 카카오 사용자 정보를 조회한다.")
    @Test
    void getUserInfo() {
        // given
        KakaoUserInfoResponse kakaoUserInfoResponse = createTestKakaoUserInfoResponse("member@email.com");

        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(kakaoUserInfoResponse);

        // when
        SocialLoginUserInfoResponse socialLoginUserInfoResponse =
                kakaoLoginServiceImpl.getUserInfo(BEARER.getType() + " access-token");

        // then
        assertThat(socialLoginUserInfoResponse)
                .extracting("email")
                .isEqualTo(kakaoUserInfoResponse.getKakaoAccount().getEmail());
    }

    @DisplayName("카카오 사용자의 이메일이 없을 경우, 아이디로 대체한다.")
    @Test
    void getUserInfo_MissingEmail() {
        // given
        KakaoUserInfoResponse kakaoUserInfoResponse = createTestKakaoUserInfoResponse("");

        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(kakaoUserInfoResponse);

        // when
        SocialLoginUserInfoResponse socialLoginUserInfoResponse =
                kakaoLoginServiceImpl.getUserInfo(BEARER.getType() + " access-token");

        // then
        assertThat(socialLoginUserInfoResponse)
                .extracting("email")
                .isEqualTo(String.valueOf(kakaoUserInfoResponse.getId()));
    }

    private KakaoUserInfoResponse createTestKakaoUserInfoResponse(String email) {
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
