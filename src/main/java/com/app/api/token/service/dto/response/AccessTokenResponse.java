package com.app.api.token.service.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccessTokenResponse {

    private String authenticationScheme;
    private String accessToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime accessTokenExpirationDateTime;

    @Builder
    private AccessTokenResponse(String authenticationScheme, String accessToken, LocalDateTime accessTokenExpirationDateTime) {
        this.authenticationScheme = authenticationScheme;
        this.accessToken = accessToken;
        this.accessTokenExpirationDateTime = accessTokenExpirationDateTime;
    }
}
