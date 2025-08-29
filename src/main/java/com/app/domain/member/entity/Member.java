package com.app.domain.member.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.member.constant.OAuthProvider;
import com.app.domain.member.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "oauth_member_unique",
                columnNames = {"oauth_id", "oauth_provider", "deleted_at"}
        )}
)
@SQLRestriction(value = "deleted_at IS NULL")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider oauthProvider;

    private LocalDateTime deletedAt;

    @Builder
    private Member(String oauthId, String name, String email, String imageUrl, Role role, OAuthProvider oauthProvider) {
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
        this.oauthProvider = oauthProvider;
    }

    public static Member create(String oauthId, String name, String email, String imageUrl, Role role, OAuthProvider oauthProvider) {
        return Member.builder()
                .oauthId(oauthId)
                .name(name)
                .email(email)
                .imageUrl(imageUrl)
                .role(role)
                .oauthProvider(oauthProvider)
                .build();
    }

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
