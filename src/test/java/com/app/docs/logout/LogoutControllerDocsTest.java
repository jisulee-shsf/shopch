package com.app.docs.logout;

import com.app.api.logout.controller.LogoutController;
import com.app.api.logout.service.LogoutService;
import com.app.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.app.global.jwt.constant.GrantType.BEARER;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutControllerDocsTest extends RestDocsSupport {

    private final LogoutService logoutService = mock(LogoutService.class);

    @Override
    protected Object initController() {
        return new LogoutController(logoutService);
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        // given
        // when & then
        mockMvc.perform(post("/api/logout")
                        .header(AUTHORIZATION, BEARER.getType() + " access-token")
                )
                .andExpect(status().isOk())
                .andDo(document("logout"));
    }
}
