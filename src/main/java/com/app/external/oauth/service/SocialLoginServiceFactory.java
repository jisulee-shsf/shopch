package com.app.external.oauth.service;

import com.app.domain.member.constant.OAuthType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SocialLoginServiceFactory {

    private static Map<String, SocialLoginService> socialLoginServices;

    public SocialLoginServiceFactory(Map<String, SocialLoginService> socialLoginServices) {
        SocialLoginServiceFactory.socialLoginServices = socialLoginServices;
    }

    public static SocialLoginService getSocialLoginService(OAuthType oauthType) {
        String socialLoginServiceBeanName = "";
        if (oauthType.equals(OAuthType.KAKAO)) {
            socialLoginServiceBeanName = "kakaoLoginServiceImpl";
        }
        return socialLoginServices.get(socialLoginServiceBeanName);
    }
}
