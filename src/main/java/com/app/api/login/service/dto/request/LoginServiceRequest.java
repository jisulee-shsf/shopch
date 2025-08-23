package com.app.api.login.service.dto.request;

import com.app.domain.member.constant.OAuthProvider;
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
