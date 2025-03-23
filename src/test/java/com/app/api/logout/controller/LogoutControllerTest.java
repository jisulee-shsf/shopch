package com.app.api.logout.controller;

import com.app.api.logout.service.LogoutService;
import com.app.global.jwt.service.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LogoutController.class)
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogoutService logoutService;

    @MockitoBean
    private TokenManager tokenManager;

    @DisplayName("회원을 로그아웃한다.")
    @Test
    void logout() throws Exception {
        // given
        // when & then
        mockMvc.perform(post("/api/logout")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk());
    }
}
