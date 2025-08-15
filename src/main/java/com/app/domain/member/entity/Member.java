package com.app.domain.member.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.member.constant.OAuthProvider;
import com.app.domain.member.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String profile;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider oauthProvider;

    @Builder
    private Member(String name, String email, Role role, String profile, OAuthProvider oauthProvider) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.profile = profile;
        this.oauthProvider = oauthProvider;
    }

    public static Member create(String name, String email, Role role, String profile, OAuthProvider oauthProvider) {
        return Member.builder()
                .name(name)
                .email(email)
                .role(role)
                .profile(profile)
                .oauthProvider(oauthProvider)
                .build();
    }
}
