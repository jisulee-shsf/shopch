package com.app.api.health.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class HealthCheckResponse {

    private final String serverId;
    private final List<String> activeProfiles;

    @Builder
    private HealthCheckResponse(String serverId, List<String> activeProfiles) {
        this.serverId = serverId;
        this.activeProfiles = activeProfiles;
    }
}
