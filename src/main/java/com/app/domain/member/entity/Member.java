package com.app.domain.member.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.member.constant.OAuthType;
import com.app.domain.member.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private OAuthType oauthType;

    private String refreshToken;
    private LocalDateTime refreshTokenExpirationDateTime;

    @Builder(toBuilder = true)
    private Member(String name, String email, Role role, String profile, OAuthType oauthType,
                   String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.profile = profile;
        this.oauthType = oauthType;
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }

    public static Member create(String name, String email, Role role, String profile, OAuthType oAuthType) {
        return Member.builder()
                .name(name)
                .email(email)
                .role(role)
                .profile(profile)
                .oauthType(oAuthType)
                .build();
    }

    public void updateRefreshToken(String refreshToken, LocalDateTime refreshTokenExpirationDateTime) {
        this.refreshToken = refreshToken;
        this.refreshTokenExpirationDateTime = refreshTokenExpirationDateTime;
    }

    public void expireRefreshToken(LocalDateTime now) {
        refreshTokenExpirationDateTime = now;
    }
}
