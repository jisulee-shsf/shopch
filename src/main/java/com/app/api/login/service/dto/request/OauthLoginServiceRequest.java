package com.app.api.login.service.dto.request;

import lombok.Getter;

@Getter
public class OauthLoginServiceRequest {

    private String memberType;

    public OauthLoginServiceRequest(String memberType) {
        this.memberType = memberType;
    }
}
