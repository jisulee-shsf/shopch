package com.app.api.health.controller;

import com.app.api.health.dto.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    private static final String SERVER_ID = UUID.randomUUID().toString();

    private final Environment environment;

    @GetMapping("/api/health")
    public ResponseEntity<HealthCheckResponse> healthCheck() {
        List<String> activeProfiles = Arrays.stream(environment.getActiveProfiles()).toList();

        return ResponseEntity.ok(HealthCheckResponse.builder()
                .serverId(SERVER_ID)
                .activeProfiles(activeProfiles)
                .build());
    }
}
