package com.app.external.oauth.dto.response;

import com.app.domain.member.constant.OAuthProvider;
import com.app.domain.member.constant.Role;
import com.app.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfo {

    private final String name;
    private final String email;
    private final String profile;
    private final OAuthProvider oauthProvider;

    @Builder
    private UserInfo(String name, String email, String profile, OAuthProvider oauthProvider) {
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.oauthProvider = oauthProvider;
    }

    public Member toMember(Role role) {
        return Member.create(name, email, role, profile, oauthProvider);
    }
}
