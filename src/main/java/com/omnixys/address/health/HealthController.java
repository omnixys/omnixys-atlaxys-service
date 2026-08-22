package com.omnixys.address.health;

import com.omnixys.cache.health.ValkeyHealthIndicator;
import com.omnixys.kafka.health.KafkaHealthIndicator;
import com.omnixys.security.auth.Public;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Public
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final DatabaseHealthIndicator database;
    private final KeycloakHealthIndicator keycloak;
    private final ObjectProvider<KafkaHealthIndicator> kafka;
    private final ObjectProvider<ValkeyHealthIndicator> cache;

    @GetMapping("/liveness")
    public ResponseEntity<HealthResponse> liveness() {
        Map<String, HealthCheck> checks = new LinkedHashMap<>();
        checks.put("app", HealthCheck.of(Health.up().build()));
        return HealthResponse.of(checks);
    }

    @GetMapping("/readiness")
    public ResponseEntity<HealthResponse> readiness() {
        Map<String, HealthCheck> checks = new LinkedHashMap<>();
        checks.put("app", HealthCheck.of(Health.up().build()));
        checks.put("database", HealthCheck.of(database.health()));

        if (keycloak.enabled()) {
            checks.put("keycloak", HealthCheck.of(keycloak.health()));
        }

        kafka.ifAvailable(bean -> checks.put("kafka", HealthCheck.of(bean.health())));
        cache.ifAvailable(bean -> checks.put("cache", HealthCheck.of(bean.health())));

        return HealthResponse.of(checks);
    }

    public record HealthResponse(String status, Map<String, HealthCheck> details) {

        static ResponseEntity<HealthResponse> of(Map<String, HealthCheck> checks) {
            boolean allUp = !checks.isEmpty() && checks.values().stream()
                    .allMatch(check -> Status.UP.getCode().equalsIgnoreCase(check.status()));
            HealthResponse body = new HealthResponse(allUp ? Status.UP.getCode() : Status.DOWN.getCode(), checks);
            return ResponseEntity.status(allUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }

    public record HealthCheck(String status, Map<String, Object> checks) {

        static HealthCheck of(Health health) {
            return new HealthCheck(health.getStatus().getCode(), health.getDetails());
        }
    }
}
