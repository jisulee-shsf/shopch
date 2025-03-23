package com.app.api.health.controller;

import com.app.global.jwt.service.TokenManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Environment environment;

    @MockitoBean
    private TokenManager tokenManager;

    @DisplayName("헬스 체크를 한다.")
    @Test
    void healthCheck() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/health")
                )
                .andExpect(status().isOk());
    }
}
