package com.omnixys.atlaxys.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

@Service
@RequiredArgsConstructor
@Slf4j
public class GlobalPostalImportService {

    private final DataSource dataSource;

    @Transactional
    public void importFile(Path file) throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            PGConnection pgConnection = connection.unwrap(PGConnection.class);
            CopyManager copyManager = pgConnection.getCopyAPI();

            log.info("Creating staging table...");

            try (Statement stmt = connection.createStatement()) {

                stmt.execute("DROP TABLE IF EXISTS atlaxys.staging_postal");

                stmt.execute("""
                CREATE TABLE atlaxys.staging_postal (
                    country_code    VARCHAR(2),
                    postal_code     VARCHAR(20),
                    place_name      VARCHAR(180),
                    admin_name1     VARCHAR(100),
                    admin_code1     VARCHAR(20),
                    admin_name2     VARCHAR(100),
                    admin_code2     VARCHAR(20),
                    admin_name3     VARCHAR(100),
                    admin_code3     VARCHAR(20),
                    latitude        DOUBLE PRECISION,
                    longitude       DOUBLE PRECISION,
                    accuracy        INTEGER
                );
            """);
            }

            log.info("COPY started...");

            try (Reader reader = Files.newBufferedReader(file)) {

                copyManager.copyIn(
                        """
                        COPY atlaxys.staging_postal
                        FROM STDIN
                        WITH (FORMAT csv, DELIMITER E'\\t')
                        """,
                        reader
                );
            }

            log.info("COPY finished. Merging...");

            try (Statement stmt = connection.createStatement()) {

                // STATES
                stmt.execute("""
                INSERT INTO atlaxys.state (country_id, name)
                SELECT DISTINCT c.id, s.admin_name1
                FROM atlaxys.staging_postal s
                JOIN atlaxys.country c
                  ON c.iso2 = s.country_code
                WHERE s.admin_name1 IS NOT NULL
                ON CONFLICT DO NOTHING;
            """);

                // CITIES
                stmt.execute("""
                INSERT INTO atlaxys.city (state_id, name)
                SELECT DISTINCT st.id, s.place_name
                FROM atlaxys.staging_postal s
                JOIN atlaxys.country c ON c.iso2 = s.country_code
                JOIN atlaxys.state st
                  ON st.country_id = c.id
                 AND st.name = s.admin_name1
                WHERE s.place_name IS NOT NULL
                ON CONFLICT DO NOTHING;
            """);

                // POSTAL CODES
                stmt.execute("""
                INSERT INTO atlaxys.postal_code
                (country_id, city_id, zip, location, accuracy)
                SELECT
                    c.id,
                    ci.id,
                    s.postal_code,
                    CASE
                        WHEN s.longitude IS NOT NULL AND s.latitude IS NOT NULL
                        THEN atlaxys.ST_SetSRID(atlaxys.ST_MakePoint(s.longitude, s.latitude), 4326)
                        ELSE NULL
                    END,
                    s.accuracy
                FROM atlaxys.staging_postal s
                JOIN atlaxys.country c ON c.iso2 = s.country_code
                JOIN atlaxys.state st
                  ON st.country_id = c.id
                 AND st.name = s.admin_name1
                JOIN atlaxys.city ci
                  ON ci.state_id = st.id
                 AND ci.name = s.place_name
                WHERE s.postal_code IS NOT NULL
                ON CONFLICT DO NOTHING;
                
            """);
            }

            log.info("Global postal import finished.");
        }
    }

}
