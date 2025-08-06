package com.app.api.login.controller.dto.request;

import com.app.api.login.service.dto.request.OAuthLoginServiceRequest;
import com.app.domain.member.constant.OAuthProvider;
import com.app.global.validator.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthLoginRequest {

    @NotBlank(message = "OAuth provider는 필수입니다.")
    @EnumValue(enumClass = OAuthProvider.class, message = "유효한 OAuth provider가 아닙니다.")
    private String oauthProvider;

    public OAuthLoginRequest(String oauthProvider) {
        this.oauthProvider = oauthProvider;
    }

    public OAuthLoginServiceRequest toServiceRequest() {
        return new OAuthLoginServiceRequest(OAuthProvider.from(oauthProvider));
    }
}
