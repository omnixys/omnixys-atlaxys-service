package com.omnixys.atlaxys;

import com.omnixys.atlaxys.config.AppProperties;
import com.omnixys.atlaxys.config.ApplicationConfig;
import com.omnixys.atlaxys.dev.DevConfig;
import com.omnixys.atlaxys.dev.FlywayDevConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import static com.omnixys.atlaxys.util.Banner.TEXT;

@SpringBootApplication(proxyBeanMethods = false)
@EnableConfigurationProperties({AppProperties.class})
@EnableJpaRepositories
@EnableWebSecurity
@EnableMethodSecurity
@EnableAsync
@SuppressWarnings({"ClassUnconnectedToPackage"})
public class AtlaxysApplication {

    public static void main(String[] args) {
        new Env();
        final var app = new SpringApplication(AtlaxysApplication.class);
        app.setBanner((_, _, out) -> out.println(TEXT));
        app.run(args);
    }
}