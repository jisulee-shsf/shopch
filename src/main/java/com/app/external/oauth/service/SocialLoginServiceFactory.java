package com.app.external.oauth.service;

import com.app.domain.member.constant.OAuthType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialLoginServiceFactory {

    private final Map<OAuthType, SocialLoginService> socialLoginServiceMap;

    public SocialLoginServiceFactory(List<SocialLoginService> socialLoginServices) {
        socialLoginServiceMap = socialLoginServices.stream()
                .collect(Collectors.toMap(
                        SocialLoginService::getOauthType,
                        Function.identity())
                );
    }

    public SocialLoginService getSocialLoginService(OAuthType oauthType) {
        return socialLoginServiceMap.get(oauthType);
    }
}
