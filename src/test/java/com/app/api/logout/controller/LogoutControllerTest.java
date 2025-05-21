package com.app.api.logout.controller;

import com.app.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutControllerTest extends ControllerTestSupport {

    @DisplayName("회원을 로그아웃한다.")
    @Test
    void logout() throws Exception {
        // given
        // when & then
        mockMvc.perform(post("/api/logout")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isNoContent());
    }
}
