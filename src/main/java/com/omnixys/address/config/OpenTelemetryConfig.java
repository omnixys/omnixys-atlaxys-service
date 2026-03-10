package com.omnixys.address.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer otelTracer() {
        return GlobalOpenTelemetry.getTracer("omnixys-address-service");
    }

}