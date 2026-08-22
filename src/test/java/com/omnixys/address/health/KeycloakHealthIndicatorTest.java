package com.omnixys.address.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class KeycloakHealthIndicatorTest {

    @Test
    void disabledWhenUriNotConfigured() {
        var indicator = new KeycloakHealthIndicator("", "master");

        assertFalse(indicator.enabled());
        assertEquals(Status.UNKNOWN.getCode(), indicator.health().getStatus().getCode());
    }
}
