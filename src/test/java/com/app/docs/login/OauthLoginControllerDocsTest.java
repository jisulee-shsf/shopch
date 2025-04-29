package com.app.docs.login;

import com.app.api.login.controller.OauthLoginController;
import com.app.api.login.dto.OauthLoginRequest;
import com.app.api.login.dto.OauthLoginResponse;
import com.app.api.login.service.OauthLoginService;
import com.app.domain.member.constant.MemberType;
import com.app.support.RestDocsSupport;
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
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OauthLoginControllerDocsTest extends RestDocsSupport {

    private final OauthLoginService oauthLoginService = mock(OauthLoginService.class);

    @Override
    protected Object initController() {
        return new OauthLoginController(oauthLoginService);
    }

    @DisplayName("로그인")
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

        given(oauthLoginService.oauthLogin(any(MemberType.class), anyString(), any(Date.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/oauth/login")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("memberType")
                                        .type(STRING)
                                        .description("회원 타입")
                        ),
                        responseFields(
                                fieldWithPath("grantType")
                                        .type(STRING)
                                        .description("인증 타입"),
                                fieldWithPath("accessToken")
                                        .type(STRING)
                                        .description("액세스 토큰"),
                                fieldWithPath("accessTokenExpirationDateTime")
                                        .type(STRING)
                                        .description("액세스 토큰 만료 일시"),
                                fieldWithPath("refreshToken")
                                        .type(STRING)
                                        .description("리프레시 토큰"),
                                fieldWithPath("refreshTokenExpirationDateTime")
                                        .type(STRING)
                                        .description("리프레시 토큰 만료 일시")
                        )
                ));
    }
}
