package com.app.api.token.controller;

import com.app.api.token.dto.AccessTokenResponse;
import com.app.api.token.service.AccessTokenService;
import com.app.global.jwt.service.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static java.time.ZoneId.systemDefault;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessTokenController.class)
class AccessTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private TokenManager tokenManager;

    @DisplayName("액세스 토큰을 재발급한다.")
    @Test
    void createAccessToken() throws Exception {
        // given
        Instant fixedFutureInstant = Instant.parse("2025-12-31T01:00:00Z");
        LocalDateTime issueDateTime = LocalDateTime.ofInstant(fixedFutureInstant, systemDefault());
        LocalDateTime accessTokenExpirationDateTime = issueDateTime.plusMinutes(15);

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
