package com.app.docs.health;

import com.app.api.health.controller.HealthCheckController;
import com.app.support.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthCheckControllerDocsTest extends RestDocsSupport {

    private final Environment environment = mock(Environment.class);

    @Override
    protected Object initController() {
        return new HealthCheckController(environment);
    }

    @DisplayName("헬스 체크 API")
    @Test
    void healthCheck() throws Exception {
        // given
        given(environment.getActiveProfiles())
                .willReturn(new String[]{"prod"});

        // when & then
        mockMvc.perform(get("/api/health")
                )
                .andExpect(status().isOk())
                .andDo(document("health-check",
                        responseFields(
                                fieldWithPath("serverId").type(STRING).description("서버 고유 아이디"),
                                fieldWithPath("activeProfiles").type(ARRAY).description("활성 프로파일 목록")
                        )
                ));
    }
}
