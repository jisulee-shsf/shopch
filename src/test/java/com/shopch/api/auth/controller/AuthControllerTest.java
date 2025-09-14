package com.shopch.api.auth.controller;

import com.shopch.api.auth.controller.dto.OAuthLoginRequest;
import com.shopch.api.auth.controller.dto.RefreshAccessTokenRequest;
import com.shopch.api.auth.service.dto.request.OAuthLoginServiceRequest;
import com.shopch.api.auth.service.dto.request.RefreshAccessTokenServiceRequest;
import com.shopch.api.auth.service.dto.response.AccessTokenResponse;
import com.shopch.api.auth.service.dto.response.OAuthLoginResponse;
import com.shopch.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static com.shopch.external.oauth.constant.OAuthProvider.KAKAO;
import static com.shopch.fixture.TimeFixture.INSTANT_NOW;
import static com.shopch.fixture.TimeFixture.TEST_TIME_ZONE;
import static com.shopch.fixture.TokenFixture.*;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTestSupport {

    private static final String CODE = "code";
    private static final String BAD_REQUEST_CODE = String.valueOf(HttpStatus.BAD_REQUEST.value());
    private static final String INVALID_OAUTH_PROVIDER = "INVALID";
    private static Stream<String> blankStringProvider() {
        return Stream.of("", " ");
    }

    @DisplayName("로그인 요청을 처리한 후, 발급된 토큰 정보를 반환한다.")
    @Test
    void oauthLogin() throws Exception {
        // given
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

        // when & then
        mockMvc.perform(post("/api/auth/oauth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("OAuth 제공자는 필수이다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("blankStringProvider")
    void oauthLogin_MissingOauthProvider(String input) throws Exception {
        // given
        OAuthLoginRequest request = OAuthLoginRequest.builder()
                .oauthProvider(input)
                .code(CODE)
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/oauth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value("[oauthProvider] OAuth 제공자는 필수입니다."));
    }

    @DisplayName("유효한 OAuth 제공자는 필수이다.")
    @Test
    void oauthLogin_InvalidOauthProvider() throws Exception {
        // given
        OAuthLoginRequest request = OAuthLoginRequest.builder()
                .oauthProvider(INVALID_OAUTH_PROVIDER)
                .code(CODE)
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/oauth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value("[oauthProvider] 유효하지 않은 OAuth 제공자입니다."));
    }

    @DisplayName("인가 코드는 필수이다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("blankStringProvider")
    void oauthLogin_MissingCode(String input) throws Exception {
        // given
        OAuthLoginRequest request = OAuthLoginRequest.builder()
                .oauthProvider(KAKAO.name())
                .code(input)
                .build();

        // when & then
        mockMvc.perform(post("/api/auth/oauth/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST_CODE))
                .andExpect(jsonPath("$.message").value("[code] 인가 코드는 필수입니다."));
    }

    @DisplayName("액세스 토큰 갱신 요청을 처리한 후, 갱신된 토큰 정보를 반환한다.")
    @Test
    void refreshAccessToken() throws Exception {
        // given
        RefreshAccessTokenRequest request = new RefreshAccessTokenRequest(REFRESH_TOKEN);

        AccessTokenResponse response = AccessTokenResponse.builder()
                .accessToken(ACCESS_TOKEN)
                .accessTokenExpiresAt(calculateExpiresAt(ACCESS_TOKEN_VALIDITY_MILLIS))
                .build();

        given(authService.refreshAccessToken(any(RefreshAccessTokenServiceRequest.class), any(Instant.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());
    }

    @DisplayName("리프레시 토큰은 필수이다.")
    @ParameterizedTest
    @NullSource
    @MethodSource("blankStringProvider")
    void refreshAccessToken_MissingRefreshToken(String input) throws Exception {
        // given
        RefreshAccessTokenRequest request = new RefreshAccessTokenRequest(input);

        // when & then
        mockMvc.perform(post("/api/auth/token/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(String.valueOf(BAD_REQUEST.value())))
                .andExpect(jsonPath("$.message").value("[refreshToken] 리프레시 토큰은 필수입니다."));
    }

    @DisplayName("로그아웃 요청을 처리한다.")
    @Test
    void logout() throws Exception {
        // when & then
        mockMvc.perform(post("/api/auth/logout")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isNoContent());
    }

    private LocalDateTime calculateExpiresAt(long validityMillis) {
        return INSTANT_NOW.plusMillis(validityMillis)
                .atZone(TEST_TIME_ZONE)
                .toLocalDateTime();
    }
}
