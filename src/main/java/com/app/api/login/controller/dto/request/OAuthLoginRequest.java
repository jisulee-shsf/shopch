package com.app.api.login.controller.dto.request;

import com.app.api.login.service.dto.request.OAuthLoginServiceRequest;
import com.app.domain.member.constant.MemberType;
import com.app.global.validator.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotBlank(message = "회원 타입은 필수입니다.")
    @EnumValue(enumClass = MemberType.class, message = "유효한 회원 타입이 아닙니다.")
    private String memberType;

    public OAuthLoginRequest(String memberType) {
        this.memberType = memberType;
    }

    public OAuthLoginServiceRequest toServiceRequest() {
        return new OAuthLoginServiceRequest(MemberType.from(memberType));
    }
}
