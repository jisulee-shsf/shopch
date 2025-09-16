package com.shopch.docs.member;

import com.shopch.api.member.controller.MemberController;
import com.shopch.api.member.service.MemberAccountService;
import com.shopch.api.member.service.dto.MemberInfoResponse;
import com.shopch.global.resolver.dto.MemberInfoDto;
import com.shopch.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.shopch.domain.member.constant.Role.USER;
import static com.shopch.fixture.TokenFixture.ACCESS_TOKEN;
import static com.shopch.global.auth.constant.AuthenticationScheme.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberControllerDocsTest extends RestDocsSupport {

    private static final Long MEMBER_ID = 1L;
    private static final String MEMBER_NAME = "member";
    private static final String MEMBER_EMAIL = "member@email.com";
    private static final String MEMBER_IMAGE_URL = "http://.../img_110x110.jpg";

    private final MemberAccountService memberAccountService = mock(MemberAccountService.class);

    @Override
    protected Object initController() {
        return new MemberController(memberAccountService);
    }

    @DisplayName("회원 정보 조회")
    @Test
    void getMemberInfo() throws Exception {
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

        mockMvc.perform(get("/api/members/me")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isOk())
                .andDo(document("member-info",
                        responseFields(
                                fieldWithPath("id").type(NUMBER).description("회원 아이디"),
                                fieldWithPath("name").type(STRING).description("회원 이름"),
                                fieldWithPath("email").type(STRING).description("회원 이메일"),
                                fieldWithPath("imageUrl").type(STRING).description("회원 이미지 URL"),
                                fieldWithPath("role").type(STRING).description("회원 역할")
                        )
                ));
    }

    @DisplayName("회원 탈퇴")
    @Test
    void deleteMember() throws Exception {
        mockMvc.perform(delete("/api/members/me")
                        .header(AUTHORIZATION, BEARER.getPrefix() + ACCESS_TOKEN)
                )
                .andExpect(status().isNoContent())
                .andDo(document("member-delete"));
    }
}
