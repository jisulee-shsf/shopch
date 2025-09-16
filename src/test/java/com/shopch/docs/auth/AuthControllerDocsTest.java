package com.shopch.docs.auth;


import com.shopch.api.auth.controller.AuthController;
import com.shopch.api.auth.controller.dto.OAuthLoginRequest;
import com.shopch.api.auth.controller.dto.RefreshAccessTokenRequest;
import com.shopch.api.auth.service.AuthService;
import com.shopch.api.auth.service.dto.request.OAuthLoginServiceRequest;
import com.shopch.api.auth.service.dto.request.RefreshAccessTokenServiceRequest;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
import com.shopch.api.auth.service.dto.response.OAuthLoginResponse;
import com.shopch.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;

import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.TEST_TIME_ZONE;
import static com.shopch.fixture.TokenFixture.*;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerDocsTest extends RestDocsSupport {

    private static final String CODE = "code";

    private final AuthService authService = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @DisplayName("로그인")
    @Test
    void oauthLogin() throws Exception {
        OAuthLoginRequest request = OAuthLoginRequest.builder()
                .oauthProvider(KAKAO.name())
                .code(CODE)
                .build();

        OAuthLoginResponse response = OAuthLoginResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .accessTokenExpiresAt(calculateExpiresAt(ACCESS_TOKEN_VALIDITY_MILLIS))
                .refreshToken(REFRESH_TOKEN)
                .refreshTokenExpiresAt(calculateExpiresAt(REFRESH_TOKEN_VALIDITY_MILLIS))
                .build();

        given(authService.oauthLogin(any(OAuthLoginServiceRequest.class), any(Instant.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/auth/oauth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("auth-oauth-login",
                        requestFields(
                                fieldWithPath("oauthProvider").type(STRING).description("OAuth 제공자"),
                                fieldWithPath("code").type(STRING).description("인가 코드")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
                                fieldWithPath("accessTokenExpiresAt").type(STRING).description("액세스 토큰 만료 일시"),
                                fieldWithPath("refreshToken").type(STRING).description("리프레시 토큰"),
                                fieldWithPath("refreshTokenExpiresAt").type(STRING).description("리프레시 토큰 만료 일시")
                        )
                ));
    }

    @DisplayName("액세스 토큰 갱신")
    @Test
    void refreshAccessToken() throws Exception {
        RefreshAccessTokenRequest request = new RefreshAccessTokenRequest(REFRESH_TOKEN);

        AccessTokenResponse response = AccessTokenResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .accessTokenExpiresAt(calculateExpiresAt(ACCESS_TOKEN_VALIDITY_MILLIS))
                .build();

        given(authService.refreshAccessToken(any(RefreshAccessTokenServiceRequest.class), any(Instant.class)))
                .willReturn(response);

        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("auth-token-refresh",
                        responseFields(
                                fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
                                fieldWithPath("accessTokenExpiresAt").type(STRING).description("액세스 토큰 만료 일시")
                        )
                ));
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(delete("/api/auth/logout")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isNoContent())
                .andDo(document("auth-logout"));
    }

    private LocalDateTime calculateExpiresAt(long validityMillis) {
        return INSTANT_NOW.plusMillis(validityMillis)
                .atZone(TEST_TIME_ZONE)
                .toLocalDateTime();
    }
}
