package com.trickyquiz.backend.api.health.dto;

public record HealthResponse(
        String status,
        String service
) {
}
