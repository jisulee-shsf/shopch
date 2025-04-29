package com.app.global.resolver;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfoRequest {

    private Long id;
    private String role;

    @Builder
    private MemberInfoRequest(Long id, String role) {
        this.id = id;
        this.role = role;
    }
}
