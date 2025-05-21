package com.app.api.health.controller;

import com.app.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthCheckControllerTest extends ControllerTestSupport {

    @DisplayName("서버 상태를 체크를 한다.")
    @Test
    void healthCheck() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/health")
                )
                .andExpect(status().isOk());
    }
}
