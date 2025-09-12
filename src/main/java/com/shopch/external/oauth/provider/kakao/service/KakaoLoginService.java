package com.shopch.external.oauth.provider.kakao.service;

import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.external.oauth.dto.UserInfo;
import com.shopch.external.oauth.provider.kakao.client.KakaoTokenClient;
import com.shopch.external.oauth.provider.kakao.client.KakaoUserInfoClient;
import com.shopch.external.oauth.provider.kakao.dto.request.KakaoTokenRequest;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoTokenResponse;
import com.shopch.external.oauth.provider.kakao.dto.response.KakaoUserInfoResponse;
import com.shopch.external.oauth.service.SocialLoginService;
import com.shopch.global.auth.constant.AuthenticationScheme;
import com.shopch.global.error.ErrorCode;
import com.shopch.global.error.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KakaoLoginService implements SocialLoginService {

    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;
    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoUserInfoClient kakaoUserInfoClient;

    public KakaoLoginService(@Value("${oauth.provider.kakao.client-id}") String clientId,
                             @Value("${oauth.provider.kakao.redirect-uri}") String redirectUri,
                             @Value("${oauth.provider.kakao.client-secret}") String clientSecret,
                             KakaoTokenClient kakaoTokenClient,
                             KakaoUserInfoClient kakaoUserInfoClient
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.clientSecret = clientSecret;
        this.kakaoTokenClient = kakaoTokenClient;
        this.kakaoUserInfoClient = kakaoUserInfoClient;
    }

    @Override
    public OAuthProvider oauthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public UserInfo getUserInfo(String code) {
        String accessToken = requestAccessToken(code);
        KakaoUserInfoResponse userInfoResponse = kakaoUserInfoClient.getKakaoUserInfo(AuthenticationScheme.BEARER.getPrefix() + accessToken);
        return UserInfo.of(userInfoResponse, oauthProvider());
    }

    private String requestAccessToken(String code) {
        KakaoTokenRequest tokenRequest = KakaoTokenRequest.of(clientId, redirectUri, code, clientSecret);
        KakaoTokenResponse tokenResponse = kakaoTokenClient.requestKakaoToken(tokenRequest);

        return Optional.ofNullable(tokenResponse)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_AUTHORIZATION_CODE))
                .getAccessToken();
    }
}
