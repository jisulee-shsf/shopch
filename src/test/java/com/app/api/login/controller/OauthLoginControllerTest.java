package com.app.api.login.controller;

import com.app.api.login.dto.OauthLoginRequest;
import com.app.api.login.dto.OauthLoginResponse;
import com.app.api.login.service.OauthLoginService;
import com.app.api.login.validator.OauthValidator;
import com.app.domain.member.constant.MemberType;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OauthLoginController.class)
class OauthLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OauthValidator oauthValidator;

    @MockitoBean
    private OauthLoginService oauthLoginService;

    @DisplayName("소셜 로그인한 회원의 액세스 토큰과 리프레시 토큰을 발급한다.")
    @Test
    void oauthLogin() throws Exception {
        // given
        OauthLoginRequest request = new OauthLoginRequest("KAKAO");

        Instant fixedFutureInstant = Instant.parse("2025-12-31T01:00:00Z");
        LocalDateTime issueDateTime = LocalDateTime.ofInstant(fixedFutureInstant, systemDefault());

        OauthLoginResponse response = OauthLoginResponse.builder()
                .grantType(BEARER.getType())
                .accessToken("access-token")
                .accessTokenExpirationDateTime(issueDateTime.plusMinutes(15))
                .refreshToken("refresh-token")
                .refreshTokenExpirationDateTime(issueDateTime.plusDays(14))
                .build();

        given(oauthLoginService.oauthLogin(any(MemberType.class), anyString(), any(Date.class)))
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
        OauthLoginRequest request = new OauthLoginRequest(null);

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
}
