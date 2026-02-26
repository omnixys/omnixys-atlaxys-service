package com.omnixys.address.dev;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.omnixys.address.dev.DevConfig.DEV;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Controller class responsible for re-populating the database in the development environment.
 * <p>
 * This resolver is intended for use only in development profiles, as it includes methods that will
 * drop and recreate the database schema, which could result in data loss if used in a production environment.
 * </p>
 * <p>
 * The resolver utilizes Flyway for database migration management, allowing it to clean the current
 * database state and re-apply all migrations from scratch.
 * </p>
 *
 * <h2>Usage</h2>
 * <p>
 * This resolver should be triggered manually by a developer or via an automated process in a development
 * environment to reset and populate the database with the initial schema and data.
 * </p>
 * <p>
 * <strong>Important:</strong> This functionality is protected by Spring profiles and will only be
 * available when the application is running in the "dev" profile.
 * </p>
 *
 * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
 * @version 1.0
 * @since 24.08.2024
 * @see Flyway
 */
@Controller
@RequestMapping("/dev")
@RequiredArgsConstructor
@Slf4j
@Profile(DEV)
public class DbPopulateController {
  private final Flyway flyway;

  /**
   * Endpoint to clean and re-populate the database.
   * <p>
   * When this method is called, the Flyway `clean()` method is invoked to drop all objects in the database,
   * followed by `migrate()` to re-apply all migrations and bring the database to its initial state.
   * </p>
   * <p>
   * The process is logged with WARN level to indicate that this operation should be used with caution.
   * </p>
   *
   * @return A {@link ResponseEntity} with a plain text message indicating the operation was successful.
   *
   * @author <a href="mailto:Caleb_g@outlook.de">Caleb Gyamfi</a>
   * @since 24.08.2024
   */
  @PostMapping(value = "db_populate", produces = TEXT_PLAIN_VALUE)
  public ResponseEntity<String> dbPopulate() {
    log.warn("Die DB wird neu geladen");
//    flyway.clean();
    flyway.migrate();
    log.warn("Die DB wurde neu geladen");
    return ok("ok");
  }
}
