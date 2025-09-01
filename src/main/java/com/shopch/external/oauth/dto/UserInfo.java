package com.shopch.external.oauth.dto;

import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.domain.member.constant.Role;
import com.shopch.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfo {

    private final String oauthId;
    private final String name;
    private final String email;
    private final String imageUrl;
    private final OAuthProvider oauthProvider;

    @Builder
    private UserInfo(String oauthId, String name, String email, String imageUrl, OAuthProvider oauthProvider) {
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.oauthProvider = oauthProvider;
    }

    public static UserInfo of(OAuthUserInfo oauthUserInfo, OAuthProvider oauthProvider) {
        return UserInfo.builder()
                .oauthId(oauthUserInfo.getOauthId())
                .name(oauthUserInfo.getName())
                .email(oauthUserInfo.getEmail())
                .imageUrl(oauthUserInfo.getImageUrl())
                .oauthProvider(oauthProvider)
                .build();
    }

    public Member toMember(Role role) {
        return Member.create(oauthId, name, email, imageUrl, role, oauthProvider);
    }
}
