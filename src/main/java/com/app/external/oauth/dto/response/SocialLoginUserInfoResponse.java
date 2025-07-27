package com.app.external.oauth.dto.response;

import com.app.domain.member.constant.MemberType;
import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SocialLoginUserInfoResponse {

    private String name;
    private String email;
    private String profile;
    private MemberType memberType;

    @Builder
    private SocialLoginUserInfoResponse(String name, String email, String profile, MemberType memberType) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.memberType = memberType;
    }

    public Member toEntity(Role role) {
        return Member.create(name, email, role, profile, memberType);
    }
}
