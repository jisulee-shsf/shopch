package com.app.api.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthLoginRequest {

    @NotBlank(message = "회원 타입은 필수입니다.")
    private String memberType;

    public OauthLoginRequest(String memberType) {
        this.memberType = memberType;
    }
}
