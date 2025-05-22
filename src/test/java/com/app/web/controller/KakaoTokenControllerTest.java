package com.app.web.controller;

import com.app.support.ControllerTestSupport;
import com.app.web.dto.request.KakaoTokenRequest;
import com.app.web.dto.response.KakaoTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class KakaoTokenControllerTest extends ControllerTestSupport {

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value(response.getTokenType()))
                .andExpect(jsonPath("$.access_token").value(response.getAccessToken()))
                .andExpect(jsonPath("$.expires_in").value(response.getExpiresIn()))
                .andExpect(jsonPath("$.refresh_token").value(response.getRefreshToken()))
                .andExpect(jsonPath("$.refresh_token_expires_in").value(response.getRefreshTokenExpiresIn()))
                .andExpect(jsonPath("$.scope").value(response.getScope()));
    }
}
