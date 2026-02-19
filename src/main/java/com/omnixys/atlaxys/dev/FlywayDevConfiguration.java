package com.omnixys.atlaxys.dev;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class FlywayDevConfiguration {

  @Bean
  public ApplicationRunner flywayCleanMigrate(Flyway flyway) {
    return args -> {
      flyway.clean();
      flyway.migrate();
    };
  }
}
