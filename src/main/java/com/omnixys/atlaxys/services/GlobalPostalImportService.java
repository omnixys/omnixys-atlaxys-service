package com.omnixys.atlaxys.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
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

    /**
     * Imports a GeoNames-style global postal file (tab-separated).
     * Fully transactional.
     * Bulk-optimized.
     * PostGIS-ready.
     */
    @Transactional
    public void importFile(Path file) throws Exception {

        log.info("Starting global postal import for file: {}", file);

        Connection connection = DataSourceUtils.getConnection(dataSource);
        connection.setAutoCommit(false);

        try {

            PGConnection pgConnection = connection.unwrap(PGConnection.class);
            CopyManager copyManager = pgConnection.getCopyAPI();

            createStagingTable(connection);

            log.info("Starting COPY into staging table...");

            try (Reader reader = Files.newBufferedReader(file)) {

                copyManager.copyIn(
                        """
                        COPY atlaxys.staging_postal
                        FROM STDIN
                        WITH (
                            FORMAT csv,
                            DELIMITER E'\\t',
                            ENCODING 'UTF8'
                        )
                        """,
                        reader
                );
            }

            log.info("COPY finished. Creating staging indexes...");
            createStagingIndexes(connection);

            log.info("Merging staging data into normalized tables...");
            mergeStates(connection);
            mergeCities(connection);
            mergePostalCodes(connection);

            log.info("Dropping staging table...");
            dropStagingTable(connection);

            connection.commit();

            log.info("Global postal import successfully completed.");

        } catch (Exception ex) {
            connection.rollback();
            log.error("Global postal import failed. Transaction rolled back.", ex);
            throw ex;
        }
    }

    // ==========================================================
    // STAGING TABLE
    // ==========================================================

    private void createStagingTable(Connection connection) throws Exception {
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
                )
            """);
        }
    }

    private void createStagingIndexes(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                CREATE INDEX idx_staging_country 
                ON atlaxys.staging_postal(country_code)
            """);

            stmt.execute("""
                CREATE INDEX idx_staging_admin1 
                ON atlaxys.staging_postal(admin_code1)
            """);

            stmt.execute("""
                CREATE INDEX idx_staging_place 
                ON atlaxys.staging_postal(place_name)
            """);

            stmt.execute("""
                CREATE INDEX idx_staging_postal 
                ON atlaxys.staging_postal(postal_code)
            """);
        }
    }

    private void dropStagingTable(Connection connection) throws Exception {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS atlaxys.staging_postal");
        }
    }

    // ==========================================================
    // MERGE LOGIC
    // ==========================================================

    private void mergeStates(Connection connection) throws Exception {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                INSERT INTO atlaxys.state (country_id, code, name)
                SELECT DISTINCT
                    c.id,
                    s.admin_code1,
                    s.admin_name1
                FROM atlaxys.staging_postal s
                JOIN atlaxys.country c
                  ON c.iso2 = s.country_code
                WHERE
                    s.admin_name1 IS NOT NULL
                    AND s.admin_name1 <> ''
                    AND s.admin_code1 IS NOT NULL
                    AND s.admin_code1 <> ''
                ON CONFLICT (country_id, code) DO NOTHING
            """);
        }
    }

    private void mergeCities(Connection connection) throws Exception {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                INSERT INTO atlaxys.city (state_id, name)
                SELECT DISTINCT
                    st.id,
                    s.place_name
                FROM atlaxys.staging_postal s
                JOIN atlaxys.country c
                  ON c.iso2 = s.country_code
                JOIN atlaxys.state st
                  ON st.country_id = c.id
                 AND st.code = s.admin_code1
                WHERE
                    s.place_name IS NOT NULL
                    AND s.place_name <> ''
                ON CONFLICT (state_id, name) DO NOTHING
            """);
        }
    }

    private void mergePostalCodes(Connection connection) throws Exception {

        try (Statement stmt = connection.createStatement()) {

            stmt.execute("""
                INSERT INTO atlaxys.postal_code
                    (city_id, zip, location, accuracy)
                SELECT
                    ci.id,
                    s.postal_code,
                    CASE
                        WHEN s.longitude IS NOT NULL
                         AND s.latitude IS NOT NULL
                        THEN ST_SetSRID(
                                ST_MakePoint(s.longitude, s.latitude),
                                4326
                             )::geography
                        ELSE NULL
                    END,
                    s.accuracy
                FROM atlaxys.staging_postal s
                JOIN atlaxys.country c
                  ON c.iso2 = s.country_code
                JOIN atlaxys.state st
                  ON st.country_id = c.id
                 AND st.code = s.admin_code1
                JOIN atlaxys.city ci
                  ON ci.state_id = st.id
                 AND ci.name = s.place_name
                WHERE
                    s.postal_code IS NOT NULL
                    AND s.postal_code <> ''
                ON CONFLICT (city_id, zip) DO NOTHING
            """);
        }
    }
}
