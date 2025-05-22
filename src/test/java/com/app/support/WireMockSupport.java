package com.app.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(
        properties = {
                "kakao-token.feign.url=http://localhost:${wiremock.server.port}",
                "kakao-user-info.feign.url=http://localhost:${wiremock.server.port}"
        }
)
public abstract class WireMockSupport {

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void beforeEach() {
        wireMockServer.stop();
        wireMockServer.start();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }
}
