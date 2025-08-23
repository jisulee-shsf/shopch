package com.app.external.oauth.service;

import com.app.domain.member.constant.OAuthProvider;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.AuthenticationException;
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
                .collect(Collectors.toUnmodifiableMap(
                        SocialLoginService::oauthProvider,
                        Function.identity())
                );
    }

    public SocialLoginService getSocialLoginService(OAuthProvider oauthProvider) {
        SocialLoginService service = socialLoginServiceMap.get(oauthProvider);
        if (service == null) {
            throw new AuthenticationException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return service;
    }
}
