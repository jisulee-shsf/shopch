package com.shopch.api.auth.controller.dto;

import com.shopch.api.auth.service.dto.request.LoginServiceRequest;
import com.shopch.external.oauth.constant.OAuthProvider;
import com.shopch.global.validator.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "OAuth 제공자는 필수입니다.")
    @ValueOfEnum(enumClass = OAuthProvider.class, message = "유효하지 않은 OAuth 제공자입니다.")
    private String oauthProvider;

    @NotBlank(message = "인가 코드는 필수입니다.")
    private String code;

    @Builder
    private LoginRequest(String oauthProvider, String code) {
        this.oauthProvider = oauthProvider;
        this.code = code;
    }

    public LoginServiceRequest toServiceRequest() {
        return LoginServiceRequest.builder()
                .oauthProvider(OAuthProvider.from(oauthProvider))
                .code(code)
                .build();
    }
}
