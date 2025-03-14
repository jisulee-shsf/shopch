package com.app.api.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthLoginRequest {

    private String memberType;

    public OauthLoginRequest(String memberType) {
        this.memberType = memberType;
    }
}
