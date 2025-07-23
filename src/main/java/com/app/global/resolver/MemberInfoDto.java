package com.app.global.resolver;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoDto {

    private Long id;
    private String role;

    @Builder
    private MemberInfoDto(Long id, String role) {
        this.id = id;
        this.role = role;
    }
}
