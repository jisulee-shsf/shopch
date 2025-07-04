package com.app.api.health.controller;

import com.app.api.health.dto.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private final Environment environment;
    private final String serverId = UUID.randomUUID().toString();

    @GetMapping("/api/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        return ResponseEntity.ok(HealthCheckResponse.builder()
                .health("OK")
                .serverId(serverId)
                .activeProfiles(Arrays.asList(environment.getActiveProfiles()))
                .build());
    }
}
