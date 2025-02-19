package com.app.health.controller;

import com.app.health.dto.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthCheckController {

    private final Environment environment;
    private final String serverId = randomUUID().toString();

    @GetMapping("/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        HealthCheckResponse response = HealthCheckResponse.builder()
                .health("OK")
                .serverId(serverId)
                .activeProfiles(asList(environment.getActiveProfiles()))
                .build();
        return ResponseEntity.ok(response);
    }
}
