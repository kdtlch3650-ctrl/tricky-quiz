package com.trickyquiz.backend.api.health;

import com.trickyquiz.backend.api.health.dto.HealthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public HealthResponse health() {
        return new HealthResponse("UP", "tricky-quiz-backend");
    }
}
