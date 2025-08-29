package com.app.domain.member.constant;

public enum OAuthProvider {

    KAKAO;

    public static OAuthProvider from(String oauthProvider) {
        return OAuthProvider.valueOf(oauthProvider.toUpperCase());
    }
}
