package com.app.api.login.dto;

import com.app.domain.member.constant.MemberType;
import com.app.global.validator.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OauthLoginRequest {

    @NotBlank(message = "회원 타입은 필수입니다.")
    @ValidEnum(enumClass = MemberType.class, message = "유효한 회원 타입이 아닙니다.")
    private String memberType;

    public OauthLoginRequest(String memberType) {
        this.memberType = memberType;
    }
}
