package com.app.docs.member;

import com.app.api.member.controller.MemberInfoController;
import com.app.api.member.dto.MemberInfoResponse;
import com.app.api.member.service.MemberInfoService;
import com.app.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.domain.member.constant.Role.USER;
import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberInfoControllerDocsTest extends RestDocsSupport {

    private final MemberInfoService memberInfoService = mock(MemberInfoService.class);

    @Override
    protected Object initController() {
        return new MemberInfoController(memberInfoService);
    }

    @DisplayName("회원 정보 조회")
    @Test
    void getMemberInfo() throws Exception {
        // given
        MemberInfoResponse response = MemberInfoResponse.builder()
                .id(1L)
                .name("member")
                .email("member@email.com")
                .profile("profile")
                .role(USER)
                .build();

        given(memberInfoService.getMemberInfo(any()))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/member/info")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk())
                .andDo(document("member-info",
                        responseFields(
                                fieldWithPath("id")
                                        .type(NUMBER)
                                        .description("회원 아이디"),
                                fieldWithPath("name")
                                        .type(STRING)
                                        .description("회원 이름"),
                                fieldWithPath("email")
                                        .type(STRING)
                                        .description("회원 이메일"),
                                fieldWithPath("profile")
                                        .type(STRING)
                                        .description("회원 프로필 이미지"),
                                fieldWithPath("role")
                                        .type(STRING)
                                        .description("회원 역할")
                        )
                ));
    }
}
