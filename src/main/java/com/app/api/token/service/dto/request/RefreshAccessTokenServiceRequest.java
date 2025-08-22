package com.app.api.token.service.dto.request;

import lombok.Getter;

@Getter
public class RefreshAccessTokenServiceRequest {

    private final String refreshToken;

    public RefreshAccessTokenServiceRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
