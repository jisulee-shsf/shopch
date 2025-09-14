package com.shopch.api.member.controller;

import com.shopch.api.member.service.dto.MemberInfoResponse;
import com.shopch.global.resolver.dto.MemberInfoDto;
import com.shopch.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberControllerTest extends ControllerTestSupport {

    private static final Long MEMBER_ID = 1L;
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String MEMBER_IMAGE_URL = "http://.../img_110x110.jpg";

    @DisplayName("로그인한 회원 정보를 조회해 반환한다.")
    @Test
    void getMemberInfo() throws Exception {
        // given
        MemberInfoDto memberInfo = MemberInfoDto.builder()
                .id(MEMBER_ID)
                .role(USER)
                .build();

        MemberInfoResponse response = MemberInfoResponse.builder()
                .id(memberInfo.getId())
                .name(MEMBER_NAME)
                .email(MEMBER_EMAIL)
                .imageUrl(MEMBER_IMAGE_URL)
                .role(memberInfo.getRole().name())
                .build();

        given(memberAccountService.getMemberInfo(any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/members/me")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isOk());
    }

    @DisplayName("로그인한 회원을 탈퇴 처리한다.")
    @Test
    void deleteMember() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/members/me")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isNoContent());
    }
}
