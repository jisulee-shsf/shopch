package com.app.api.member.controller;

import com.app.api.member.dto.MemberInfoResponse;
import com.app.api.member.service.MemberInfoService;
import com.app.domain.member.constant.Role;
import com.app.global.jwt.service.TokenManager;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.app.domain.member.constant.Role.USER;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static com.app.global.jwt.constant.TokenType.ACCESS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberInfoController.class)
class MemberInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberInfoService memberInfoService;

    @MockitoBean
    private TokenManager tokenManager;

    private Claims mockClaims;

    @BeforeEach
    void setup() {
        willDoNothing().given(tokenManager).validateToken(anyString());

        mockClaims = mock(Claims.class);
        given(tokenManager.getTokenClaims(anyString())).willReturn(mockClaims);
        given(mockClaims.getSubject()).willReturn(ACCESS.name());
        given(mockClaims.get("memberId", Long.class)).willReturn(1L);
        given(mockClaims.get("role", String.class)).willReturn(USER.name());
    }

    @DisplayName("로그인한 회원 정보를 조회한다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        Long memberId = mockClaims.get("memberId", Long.class);
        Role role = Role.valueOf(mockClaims.get("role", String.class));

        MemberInfoResponse response = MemberInfoResponse.builder()
                .id(memberId)
                .name("member")
                .email("member@email.com")
                .profile("profile")
                .role(role)
                .build();

        given(memberInfoService.getMemberInfo(anyLong()))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/member/info")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk());
    }
}
