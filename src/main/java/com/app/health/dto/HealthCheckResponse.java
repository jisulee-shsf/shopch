package com.app.health.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class HealthCheckResponse {

    private String health;
    private String serverId;
    private List<String> activeProfiles;

    @Builder
    private HealthCheckResponse(String health, String serverId, List<String> activeProfiles) {
        this.health = health;
        this.serverId = serverId;
        this.activeProfiles = activeProfiles;
    }
}
