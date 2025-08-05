package com.app.api.login.service.dto.request;

import com.app.domain.member.constant.OAuthType;
import lombok.Getter;

@Getter
public class OAuthLoginServiceRequest {

    private OAuthType oAuthType;

    public OAuthLoginServiceRequest(OAuthType oauthType) {
        this.oAuthType = oauthType;
    }
}
