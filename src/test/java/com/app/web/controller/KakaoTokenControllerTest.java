package com.app.web.controller;

import com.app.global.jwt.service.TokenManager;
import com.app.web.client.KakaoTokenClient;
import com.app.web.dto.KakaoTokenRequest;
import com.app.web.dto.KakaoTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KakaoTokenController.class)
class KakaoTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KakaoTokenClient kakaoTokenClient;

    @MockitoBean
    private TokenManager tokenManager;

    @DisplayName("카카오 인가 코드로 토큰 발급을 요청한다.")
    @Test
    void loginCallback() throws Exception {
        // given
        KakaoTokenResponse response = KakaoTokenResponse.builder()
                .tokenType("bearer")
                .accessToken("access-token")
                .expiresIn(21599)
                .refreshToken("refresh-token")
                .refreshTokenExpiresIn(5183999)
                .scope("account-email profile-image profile-nickname")
                .build();

        given(kakaoTokenClient.requestKakaoToken(any(KakaoTokenRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/oauth/kakao/callback")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value(response.getTokenType()))
                .andExpect(jsonPath("$.access_token").value(response.getAccessToken()))
                .andExpect(jsonPath("$.expires_in").value(response.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token").value(response.getRefreshToken()))
                .andExpect(jsonPath("$.refresh_token_expires_in").value(response.getRefreshTokenExpiresIn()))
                .andExpect(jsonPath("$.scope").value(response.getScope()));
    }
}
