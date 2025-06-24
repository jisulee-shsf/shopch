package com.app.docs.token;

import com.app.api.token.controller.AccessTokenController;
import com.app.api.token.service.AccessTokenService;
import com.app.api.token.service.dto.response.AccessTokenResponse;
import com.app.support.RestDocsSupport;
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
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccessTokenControllerDocsTest extends RestDocsSupport {

    private final AccessTokenService accessTokenService = mock(AccessTokenService.class);

    @Override
    protected Object initController() {
        return new AccessTokenController(accessTokenService);
    }

    @DisplayName("액세스 토큰 재발급")
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
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("token-issue",
                        responseFields(
                                fieldWithPath("grantType").type(STRING).description("인증 타입"),
                                fieldWithPath("accessToken").type(STRING).description("액세스 토큰"),
                                fieldWithPath("accessTokenExpirationDateTime").type(STRING).description("액세스 토큰 만료 일시")
                        )
                ));
    }
}
