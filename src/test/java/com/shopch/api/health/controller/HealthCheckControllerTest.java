package com.shopch.api.health.controller;

import com.shopch.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthCheckControllerTest extends ControllerTestSupport {

    @DisplayName("서버 상태를 체크를 한다.")
    @Test
    void healthCheck() throws Exception {
        // when & then
        mockMvc.perform(get("/api/health")
                )
                .andExpect(status().isOk());
    }
}
