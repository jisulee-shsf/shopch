package com.shopch.api.auth.service.dto.request;

import com.shopch.external.oauth.constant.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginServiceRequest {

    private final OAuthProvider oauthProvider;
    private final String code;

    @Builder
    private LoginServiceRequest(OAuthProvider oauthProvider, String code) {
        this.oauthProvider = oauthProvider;
        this.code = code;
    }
}
