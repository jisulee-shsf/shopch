package com.shopch.external.oauth.service;

import com.shopch.external.oauth.provider.kakao.service.KakaoLoginService;
import com.shopch.global.error.exception.AuthException;
import com.shopch.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.global.error.ErrorCode.UNSUPPORTED_OAUTH_PROVIDER;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SocialLoginServiceRegistryTest extends IntegrationTestSupport {

    @Autowired
    private SocialLoginServiceRegistry socialLoginServiceRegistry;

    @Autowired
    private KakaoLoginService kakaoLoginService;

    @DisplayName("OAuth 제공자에 해당하는 SocialLoginService 구현체를 반환한다.")
    @Test
    void getSocialLoginService() {
        // when
        SocialLoginService service = socialLoginServiceRegistry.getSocialLoginService(KAKAO);

        // then
        assertThat(service).isSameAs(kakaoLoginService);
        assertThat(service).isInstanceOf(SocialLoginService.class);
    }

    @DisplayName("OAuth 제공자에 해당하는 SocialLoginService 구현체가 없을 경우, 예외가 발생한다.")
    @Test
    void getSocialLoginService_UnsupportedOauthProvider() {
        // given
        SocialLoginServiceRegistry socialLoginServiceRegistry = new SocialLoginServiceRegistry(emptyList());

        // when & then
        assertThatThrownBy(() -> socialLoginServiceRegistry.getSocialLoginService(KAKAO))
                .isInstanceOf(AuthException.class)
                .hasMessage(UNSUPPORTED_OAUTH_PROVIDER.getMessage());
    }
}
