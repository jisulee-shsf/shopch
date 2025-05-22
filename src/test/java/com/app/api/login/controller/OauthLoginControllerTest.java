package com.app.api.login.controller;

import com.app.api.login.controller.dto.request.OauthLoginRequest;
import com.app.api.login.service.dto.request.OauthLoginServiceRequest;
import com.app.api.login.service.dto.response.OauthLoginResponse;
import com.app.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

import static com.app.fixture.TimeFixture.FIXED_INSTANT;
import static com.app.fixture.TimeFixture.FIXED_TIME_ZONE;
import static com.app.fixture.TokenFixture.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.app.fixture.TokenFixture.REFRESH_TOKEN_EXPIRATION_TIME;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OauthLoginControllerTest extends ControllerTestSupport {

    @DisplayName("소셜 로그인한 회원의 액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void oauthLogin() throws Exception {
        // given
        OauthLoginRequest request = new OauthLoginRequest("KAKAO");

        LocalDateTime issueDateTime = LocalDateTime.ofInstant(FIXED_INSTANT, FIXED_TIME_ZONE);
        LocalDateTime accessTokenExpirationDateTime = issueDateTime.plus(ACCESS_TOKEN_EXPIRATION_TIME, MILLIS);
        LocalDateTime refreshTokenExpirationDateTime = issueDateTime.plus(REFRESH_TOKEN_EXPIRATION_TIME, MILLIS);

        OauthLoginResponse response = OauthLoginResponse.builder()
                .grantType(BEARER.getType())
                .accessToken("access-token")
                .accessTokenExpirationDateTime(accessTokenExpirationDateTime)
                .refreshToken("refresh-token")
                .refreshTokenExpirationDateTime(refreshTokenExpirationDateTime)
                .build();

        given(oauthLoginService.oauthLogin(any(OauthLoginServiceRequest.class), anyString(), any(Date.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/oauth/login")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("회원 타입은 필수이다.")
    @Test
    void oauthLogin_MissingMemberType() throws Exception {
        // given
        OauthLoginRequest request = new OauthLoginRequest("");

        // when & then
        mockMvc.perform(post("/api/oauth/login")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[memberType] 회원 타입은 필수입니다."));
    }

    @DisplayName("유효한 회원 타입은 필수이다.")
    @Test
    void oauthLogin_InvalidMemberType() throws Exception {
        // given
        OauthLoginRequest request = new OauthLoginRequest("invalid-member-type");

        // when & then
        mockMvc.perform(post("/api/oauth/login")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("[memberType] 유효한 회원 타입이 아닙니다."));
    }
}
