package com.shopch.external.oauth.service;

import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
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
            throw new AuthException(ErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return service;
    }
}
