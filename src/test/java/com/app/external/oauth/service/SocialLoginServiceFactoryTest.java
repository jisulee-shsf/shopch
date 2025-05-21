package com.app.external.oauth.service;

import com.app.external.oauth.kakao.service.KakaoLoginServiceImpl;
import com.app.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.app.domain.member.constant.MemberType.GOOGLE;
import static com.app.domain.member.constant.MemberType.KAKAO;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SocialLoginServiceFactoryTest extends IntegrationTestSupport {

    @Autowired
    private KakaoLoginServiceImpl kakaoLoginServiceImpl;

    @DisplayName("회원 타입에 해당하는 SocialLoginService 구현체를 반환한다.")
    @Test
    void getSocialLoginService() {
        // given
        // when
        SocialLoginService service = SocialLoginServiceFactory.getSocialLoginService(KAKAO);

        // then
        assertThat(service.getClass()).isEqualTo(kakaoLoginServiceImpl.getClass());
        assertThat(service).isInstanceOf(SocialLoginService.class);
    }

    @DisplayName("회원 타입에 해당하는 SocialLoginService 구현체가 없을 경우, null을 반환한다.")
    @Test
    void getSocialLoginService_NoImplementation() {
        // given
        // when
        SocialLoginService service = SocialLoginServiceFactory.getSocialLoginService(GOOGLE);

        // then
        assertThat(service).isNull();
    }
}
