package com.app.api.token.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccessTokenResponse {

    private final String accessToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime accessTokenExpiresAt;

    @Builder
    private AccessTokenResponse(String accessToken, LocalDateTime accessTokenExpiresAt) {
        this.accessToken = accessToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    public static AccessTokenResponse of(String accessToken, LocalDateTime accessTokenExpiresAt) {
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .build();
    }
}
