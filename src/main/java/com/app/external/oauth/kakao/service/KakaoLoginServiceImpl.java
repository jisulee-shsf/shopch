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
import org.springframework.util.StringUtils;

@Service
public class KakaoLoginServiceImpl implements SocialLoginService {

    private final String clientId;
    private final String redirectUri;
    private final String clientSecret;

    private final KakaoTokenClient kakaoTokenClient;
    private final KakaoUserInfoClient kakaoUserInfoClient;

    public KakaoLoginServiceImpl(@Value("${kakao.client.id}") String clientId,
                                 @Value("${kakao.redirect.uri}") String redirectUri,
                                 @Value("${kakao.client.secret}") String clientSecret,
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
        KakaoTokenRequest request = KakaoTokenRequest.of(clientId, redirectUri, code, clientSecret);
        KakaoTokenResponse response = kakaoTokenClient.requestKakaoToken(request);

        KakaoUserInfoResponse userInfo = kakaoUserInfoClient.getKakaoUserInfo(AuthenticationScheme.BEARER.getPrefix() + response.getAccessToken());
        KakaoUserInfoResponse.KakaoAccount account = userInfo.getKakaoAccount();
        String email = account.getEmail();

        return UserInfo.builder()
                .name(account.getProfile().getNickname())
                .email(StringUtils.hasText(email) ? email : String.valueOf(userInfo.getId()))
                .profile(account.getProfile().getThumbnailImageUrl())
                .oauthProvider(oauthProvider())
                .build();
    }
}
