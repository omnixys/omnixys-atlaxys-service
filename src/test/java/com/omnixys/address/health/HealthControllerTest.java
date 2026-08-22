package com.omnixys.address.health;

import com.omnixys.cache.health.ValkeyHealthIndicator;
import com.omnixys.kafka.health.KafkaHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private DatabaseHealthIndicator database;
    @Mock
    private KeycloakHealthIndicator keycloak;
    @Mock
    private KafkaHealthIndicator kafkaHealth;
    @Mock
    private ValkeyHealthIndicator cacheHealth;

    private DefaultListableBeanFactory beanFactory;
    private HealthController controller;

    @BeforeEach
    void setUp() {
        beanFactory = new DefaultListableBeanFactory();
        ObjectProvider<KafkaHealthIndicator> kafka =
                beanFactory.getBeanProvider(ResolvableType.forClass(KafkaHealthIndicator.class));
        ObjectProvider<ValkeyHealthIndicator> cache =
                beanFactory.getBeanProvider(ResolvableType.forClass(ValkeyHealthIndicator.class));
        controller = new HealthController(database, keycloak, kafka, cache);
    }

    @Test
    void livenessReportsOnlyApp() {
        var response = controller.liveness().getBody();

        assertEquals("UP", response.status());
        assertEquals(Set.of("app"), response.details().keySet());
    }

    @Test
    void readinessAggregatesAllIndicators() {
        beanFactory.registerSingleton("kafkaHealth", kafkaHealth);
        beanFactory.registerSingleton("cacheHealth", cacheHealth);
        when(database.health()).thenReturn(Health.up().build());
        when(keycloak.enabled()).thenReturn(true);
        when(keycloak.health()).thenReturn(Health.up().build());
        when(kafkaHealth.health()).thenReturn(Health.up().build());
        when(cacheHealth.health()).thenReturn(Health.up().build());

        var response = controller.readiness();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().status());
        assertEquals(Set.of("app", "database", "keycloak", "kafka", "cache"), response.getBody().details().keySet());
    }

    @Test
    void readinessSkipsMissingIndicators() {
        when(database.health()).thenReturn(Health.up().build());
        when(keycloak.enabled()).thenReturn(false);

        var response = controller.readiness();

        assertEquals(Set.of("app", "database"), response.getBody().details().keySet());
    }

    @Test
    void readinessReportsDownWhenDatabaseDown() {
        when(database.health()).thenReturn(Health.down().build());
        when(keycloak.enabled()).thenReturn(false);

        var response = controller.readiness();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("DOWN", response.getBody().status());
        assertEquals("DOWN", response.getBody().details().get("database").status());
    }
}
