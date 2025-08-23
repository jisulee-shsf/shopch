package com.app.api.member.service.dto.response;

import com.app.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String profile;
    private final String role;

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
