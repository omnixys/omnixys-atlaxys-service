package com.omnixys.address.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class KeycloakHealthIndicator implements HealthIndicator {

    private final RestClient restClient;
    private final String healthUrl;

    public KeycloakHealthIndicator(
            @Value("${app.keycloak.uri:}") String uri,
            @Value("${app.keycloak.realm:}") String realm
    ) {
        if (uri != null && !uri.isBlank()) {
            this.healthUrl = uri + "/realms/" + realm + "/.well-known/openid-configuration";
            this.restClient = RestClient.create();
        } else {
            this.healthUrl = null;
            this.restClient = null;
        }
    }

    public boolean enabled() {
        return healthUrl != null;
    }

    @Override
    public Health health() {
        if (healthUrl == null) {
            return Health.unknown().build();
        }
        try {
            ResponseEntity<Void> response = restClient.get()
                    .uri(healthUrl)
                    .retrieve()
                    .toBodilessEntity();
            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("url", healthUrl).build();
            }
            return Health.down().withDetail("url", healthUrl).build();
        } catch (Exception e) {
            log.error("Keycloak health check failed: {}", e.getMessage());
            return Health.down(e).withDetail("url", healthUrl).build();
        }
    }
}
