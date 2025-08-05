package com.app.api.login.controller.dto.request;

import com.app.api.login.service.dto.request.OAuthLoginServiceRequest;
import com.app.domain.member.constant.OAuthType;
import com.app.global.validator.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotBlank(message = "OAuth 타입은 필수입니다.")
    @EnumValue(enumClass = OAuthType.class, message = "유효한 OAuth 타입이 아닙니다.")
    private String oauthType;

    public OAuthLoginRequest(String oauthType) {
        this.oauthType = oauthType;
    }

    public OAuthLoginServiceRequest toServiceRequest() {
        return new OAuthLoginServiceRequest(OAuthType.from(oauthType));
    }
}
