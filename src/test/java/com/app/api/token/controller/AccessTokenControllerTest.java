package com.app.api.token.controller;

import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.fixture.TokenFixture.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccessTokenControllerTest extends ControllerTestSupport {

    @DisplayName("액세스 토큰을 재발급한다.")
    @Test
    void createAccessToken() throws Exception {
        // given
        LocalDateTime issueDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime accessTokenExpirationDateTime = issueDateTime.plus(ACCESS_TOKEN_EXPIRATION_TIME, MILLIS);

        AccessTokenResponse response = AccessTokenResponse.builder()
                .grantType(BEARER.getType())
                .accessToken("access-token")
                .accessTokenExpirationDateTime(accessTokenExpirationDateTime)
                .build();

        given(accessTokenService.createAccessTokenByRefreshToken(anyString(), any(Date.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/access-token/issue")
                        .header(AUTHORIZATION, BEARER.getType() + " refresh-token")
                )
                .andExpect(status().isOk());
    }
}
