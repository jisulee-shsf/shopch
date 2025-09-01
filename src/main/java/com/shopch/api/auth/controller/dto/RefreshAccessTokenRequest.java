package com.shopch.api.auth.controller.dto;

import com.shopch.api.auth.service.dto.request.RefreshAccessTokenServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RefreshAccessTokenRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

    public RefreshAccessTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshAccessTokenServiceRequest toServiceRequest() {
        return new RefreshAccessTokenServiceRequest(refreshToken);
    }
}
