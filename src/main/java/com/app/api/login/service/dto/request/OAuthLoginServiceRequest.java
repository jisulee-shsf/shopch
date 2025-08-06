package com.app.api.login.service.dto.request;

import com.app.domain.member.constant.OAuthProvider;
import lombok.Getter;

@Getter
public class OAuthLoginServiceRequest {

    private OAuthProvider oauthProvider;

    public OAuthLoginServiceRequest(OAuthProvider oauthProvider) {
        this.oauthProvider = oauthProvider;
    }
}
