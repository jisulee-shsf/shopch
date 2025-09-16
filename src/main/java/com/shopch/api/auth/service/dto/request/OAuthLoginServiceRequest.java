package com.shopch.api.auth.service.dto.request;

import com.shopch.external.oauth.constant.OAuthProvider;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthLoginServiceRequest {

    private final OAuthProvider oauthProvider;
    private final String code;

    @Builder
    private OAuthLoginServiceRequest(OAuthProvider oauthProvider, String code) {
        this.oauthProvider = oauthProvider;
        this.code = code;
    }
}
