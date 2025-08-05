package com.app.api.login.service.dto.request;

import com.app.domain.member.constant.MemberType;
import lombok.Getter;

@Getter
public class OAuthLoginServiceRequest {

    private MemberType memberType;

    public OAuthLoginServiceRequest(MemberType memberType) {
        this.memberType = memberType;
    }
}
