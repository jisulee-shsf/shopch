package com.app.external.oauth.kakao.service;

import com.app.domain.member.constant.OAuthProvider;
import com.app.external.oauth.dto.response.UserInfo;
import com.app.external.oauth.kakao.client.KakaoUserInfoClient;
import com.app.external.oauth.kakao.dto.response.KakaoUserInfoResponse;
import com.app.external.oauth.service.SocialLoginService;
import com.app.global.jwt.constant.AuthenticationScheme;
import com.app.web.client.KakaoTokenClient;
import com.app.web.dto.request.KakaoTokenRequest;
import com.app.web.dto.response.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public String requestAccessToken(String code) {
        KakaoTokenRequest tokenRequest = KakaoTokenRequest.of(clientId, redirectUri, code, clientSecret);
        KakaoTokenResponse tokenResponse = kakaoTokenClient.requestKakaoToken(tokenRequest);
        return tokenResponse.getAccessToken();
    }
}
