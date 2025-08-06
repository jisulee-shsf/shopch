package com.app.external.oauth.service;

import com.app.domain.member.constant.OAuthProvider;
import com.app.global.error.ErrorType;
import com.app.global.error.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialLoginServiceFactory {

    private final Map<OAuthProvider, SocialLoginService> socialLoginServiceMap;

    public SocialLoginServiceFactory(List<SocialLoginService> socialLoginServices) {
        socialLoginServiceMap = socialLoginServices.stream()
                .collect(Collectors.toMap(
                        SocialLoginService::getOauthProvider,
                        Function.identity())
                );
    }

    public SocialLoginService getSocialLoginService(OAuthProvider oauthProvider) {
        if (isUnsupportedOauthProvider(oauthProvider)) {
            throw new BusinessException(ErrorType.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return socialLoginServiceMap.get(oauthProvider);
    }

    private boolean isUnsupportedOauthProvider(OAuthProvider oauthProvider) {
        return !socialLoginServiceMap.containsKey(oauthProvider);
    }
}
