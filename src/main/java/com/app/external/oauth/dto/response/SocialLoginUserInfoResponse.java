package com.app.external.oauth.dto.response;

import com.app.domain.member.constant.OAuthProvider;
import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SocialLoginUserInfoResponse {

    private String name;
    private String email;
    private String profile;
    private OAuthProvider oauthProvider;

    @Builder
    private SocialLoginUserInfoResponse(String name, String email, String profile, OAuthProvider oauthProvider) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.oauthProvider = oauthProvider;
    }

    public Member toEntity(Role role) {
        return Member.create(name, email, role, profile, oauthProvider);
    }
}
