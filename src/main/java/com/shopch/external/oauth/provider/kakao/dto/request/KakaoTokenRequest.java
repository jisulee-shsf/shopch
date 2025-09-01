package com.shopch.external.oauth.provider.kakao.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoTokenRequest {

    private static final String GRANT_TYPE = "authorization_code";

    private final String grant_type;
    private final String client_id;
    private final String redirect_uri;
    private final String code;
    private final String client_secret;

    @Builder
    private KakaoTokenRequest(String grantType, String clientId, String redirectUri, String code, String clientSecret) {
        grant_type = grantType;
        client_id = clientId;
        redirect_uri = redirectUri;
        this.code = code;
        client_secret = clientSecret;
    }

    public static KakaoTokenRequest of(String clientId, String redirectUri, String code, String clientSecret) {
        return KakaoTokenRequest.builder()
                .grantType(GRANT_TYPE)
                .clientId(clientId)
                .redirectUri(redirectUri)
                .code(code)
                .clientSecret(clientSecret)
                .build();
    }
}
