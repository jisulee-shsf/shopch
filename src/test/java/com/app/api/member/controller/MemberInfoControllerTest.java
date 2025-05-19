package com.app.api.member.controller;

import com.app.api.member.service.MemberInfoService;
import com.app.api.member.service.dto.response.MemberInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.app.domain.member.constant.Role.USER;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = MemberInfoController.class,
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {
                        WebMvcConfigurer.class,
                        HandlerInterceptor.class,
                        HandlerMethodArgumentResolver.class
                }
        )
)
class MemberInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberInfoService memberInfoService;

    @DisplayName("로그인한 회원 정보를 조회한다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        MemberInfoResponse response = MemberInfoResponse.builder()
                .id(1L)
                .name("member")
                .email("member@email.com")
                .profile("profile")
                .role(USER.name())
                .build();

        given(memberInfoService.getMemberInfo(any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/member/info")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk());
    }
}
