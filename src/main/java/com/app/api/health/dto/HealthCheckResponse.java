package com.app.api.health.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class HealthCheckResponse {

    private String serverId;
    private List<String> activeProfiles;

    @Builder
    private HealthCheckResponse(String serverId, List<String> activeProfiles) {
        this.serverId = serverId;
        this.activeProfiles = activeProfiles;
    }
}
