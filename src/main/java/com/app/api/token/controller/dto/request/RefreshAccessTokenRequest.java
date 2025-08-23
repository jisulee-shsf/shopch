package com.app.api.token.controller.dto.request;

import com.app.api.token.service.dto.request.RefreshAccessTokenServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshAccessTokenRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

    public RefreshAccessTokenServiceRequest toServiceRequest() {
        return new RefreshAccessTokenServiceRequest(refreshToken);
    }
}
