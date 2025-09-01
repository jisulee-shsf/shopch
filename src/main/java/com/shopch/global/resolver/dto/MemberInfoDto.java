package com.shopch.global.resolver.dto;

import com.shopch.domain.member.constant.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoDto {

    private Long id;
    private Role role;

    @Builder
    private MemberInfoDto(Long id, Role role) {
        this.id = id;
        this.role = role;
    }
}
