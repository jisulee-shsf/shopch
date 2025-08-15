package com.app.domain.token.entity;

import com.app.domain.common.BaseEntity;
import com.app.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true, nullable = false)
    private Member member;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expirationDateTime;

    @Builder
    private RefreshToken(Member member, String token, LocalDateTime expirationDateTime) {
        this.member = member;
        this.token = token;
        this.expirationDateTime = expirationDateTime;
    }

    public static RefreshToken create(Member member, String token, LocalDateTime expirationDateTime) {
        return RefreshToken.builder()
                .member(member)
                .token(token)
                .expirationDateTime(expirationDateTime)
                .build();
    }

    public void updateToken(String token, LocalDateTime expirationDateTime) {
        this.token = token;
        this.expirationDateTime = expirationDateTime;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(expirationDateTime);
    }
}
