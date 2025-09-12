package com.shopch.external.oauth.provider.kakao.service;

import com.shopch.external.oauth.dto.UserInfo;
import com.shopch.external.oauth.provider.kakao.client.KakaoTokenClient;
import com.shopch.external.oauth.provider.kakao.client.KakaoUserInfoClient;
import com.shopch.external.oauth.provider.kakao.dto.request.KakaoTokenRequest;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoTokenResponse;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoUserInfoResponse;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.fixture.TokenFixture.REFRESH_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KakaoLoginServiceTest {

    private static final int EXPIRES_IN = 3600;
    private static final String SCOPE = "account-email profile-image profile-nickname";
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "user";
    private static final String USER_EMAIL = "user@email.com";
    private static final String USER_IMAGE_URL = "http://yyy.kakao.com/.../img_110x110.jpg";
    private static final String CODE = "code";
    private static final String INVALID_CODE = "invalid_code";

    @Mock
    private KakaoTokenClient kakaoTokenClient;

    @Mock
    private KakaoUserInfoClient kakaoUserInfoClient;

    @InjectMocks
    private KakaoLoginService kakaoLoginService;

    @DisplayName("OAuth 제공자를 반환한다.")
    @Test
    void oauthProvider() {
        // when & then
        assertThat(kakaoLoginService.oauthProvider()).isEqualTo(KAKAO);
    }

    @DisplayName("인가 코드로 카카오 사용자 정보를 받는다.")
    @Test
    void getUserInfo() {
        // given
        given(kakaoTokenClient.requestKakaoToken(any(KakaoTokenRequest.class)))
                .willReturn(createKakaoTokenResponse());

        given(kakaoUserInfoClient.getKakaoUserInfo(anyString()))
                .willReturn(createKakaoUserInfoResponse(USER_ID));

        // when
        UserInfo userInfo = kakaoLoginService.getUserInfo(CODE);

        // then
        assertThat(userInfo)
                .extracting(
                        UserInfo::getOauthId,
                        UserInfo::getName,
                        UserInfo::getEmail,
                        UserInfo::getImageUrl,
                        UserInfo::getOauthProvider
                )
                .containsExactly(
                        String.valueOf(USER_ID),
                        USER_NAME,
                        USER_EMAIL,
                        USER_IMAGE_URL,
                        KAKAO
                );
    }

    @DisplayName("유효하지 않은 인가 코드로 카카오 토큰 정보를 받지 못했을 때 사용자 정보 요청을 시도할 경우, 예외가 발생한다.")
    @Test
    void getUserInfo_InvalidCode() {
        // given
        given(kakaoTokenClient.requestKakaoToken(any(KakaoTokenRequest.class)))
                .willReturn(null);

        // when & then
        assertThatThrownBy(() -> kakaoLoginService.getUserInfo(INVALID_CODE))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage(ErrorCode.INVALID_AUTHORIZATION_CODE.getMessage());
    }

    private KakaoTokenResponse createKakaoTokenResponse() {
        return KakaoTokenResponse.builder()
                .tokenType(BEARER.getText())
                .accessToken(ACCESS_TOKEN)
                .expiresIn(EXPIRES_IN)
                .refreshToken(REFRESH_TOKEN)
                .refreshTokenExpiresIn(EXPIRES_IN)
                .scope(SCOPE)
                .build();
    }

    private KakaoUserInfoResponse createKakaoUserInfoResponse(Long userId) {
        return KakaoUserInfoResponse.builder()
                .id(userId)
                .kakaoAccount(KakaoUserInfoResponse.KakaoAccount.builder()
                        .email(USER_EMAIL)
                        .profile(KakaoUserInfoResponse.KakaoAccount.Profile.builder()
                                .nickname(USER_NAME)
                                .thumbnailImageUrl(USER_IMAGE_URL)
                                .build())
                        .build())
                .build();
    }
}
