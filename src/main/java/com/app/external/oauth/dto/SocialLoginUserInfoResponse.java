package com.app.external.oauth.dto;

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
    private String memberType;

    @Builder
    private SocialLoginUserInfoResponse(String name, String email, String profile, String memberType) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.memberType = memberType;
    }

    public Member toEntity(Role role) {
        return Member.builder()
                .name(name)
                .email(email)
                .profile(profile)
                .memberType(MemberType.from(memberType))
                .role(role)
                .build();
    }
}
