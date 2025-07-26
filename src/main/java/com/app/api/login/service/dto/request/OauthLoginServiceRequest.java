package com.app.api.login.service.dto.request;

import com.app.domain.member.constant.MemberType;
import lombok.Getter;

@Getter
public class OauthLoginServiceRequest {

    private MemberType memberType;

    public OauthLoginServiceRequest(MemberType memberType) {
        this.memberType = memberType;
    }
}
