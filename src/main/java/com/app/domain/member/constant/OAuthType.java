package com.app.domain.member.constant;

public enum OAuthType {

    KAKAO,
    GOOGLE;

    public static OAuthType from(String oauthType) {
        return OAuthType.valueOf(oauthType.toUpperCase());
    }
}
