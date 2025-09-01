package com.shopch.api.member.service.dto;

import com.shopch.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final String imageUrl;
    private final String role;

    @Builder
    private MemberInfoResponse(Long id, String name, String email, String imageUrl, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
    }

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .imageUrl(member.getImageUrl())
                .role(member.getRole().name())
                .build();
    }
}
