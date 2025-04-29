package com.app.api.member.dto;

import com.app.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

    private Long id;
    private String name;
    private String email;
    private String profile;
    private String role;

    @Builder
    private MemberInfoResponse(Long id, String name, String email, String profile, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profile = profile;
        this.role = role;
    }

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .profile(member.getProfile())
                .role(member.getRole().name())
                .build();
    }
}
