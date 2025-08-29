package com.app.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public abstract class WireMockSupport {

    @Autowired
    protected WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();
    }
}
