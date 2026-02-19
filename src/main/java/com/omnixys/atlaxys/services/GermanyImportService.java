package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.entity.City;
import com.omnixys.atlaxys.models.entity.Country;
import com.omnixys.atlaxys.models.entity.PostalCode;
import com.omnixys.atlaxys.models.entity.State;
import com.omnixys.atlaxys.repository.CityRepository;
import com.omnixys.atlaxys.repository.CountryRepository;
import com.omnixys.atlaxys.repository.PostalCodeRepository;
import com.omnixys.atlaxys.repository.StateRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GermanyImportService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final PostalCodeRepository postalRepository;
    private final EntityManager entityManager;

    private static final int BATCH_SIZE = 1000;

    private final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    @Transactional
    public void importGermany(Path file) throws IOException {

        log.info("Starting Germany postal import from {}", file);

        Country germany = countryRepository
                .findByIso2("DE")
                .orElseThrow(() ->
                        new IllegalStateException("Country DE not found. Seed countries first.")
                );

        Map<String, State> stateCache = new HashMap<>();
        Map<String, City> cityCache = new HashMap<>();

        // preload existing states
        stateRepository.findByCountry(germany)
                .forEach(s -> stateCache.put(s.getName(), s));

        int batchCounter = 0;
        int totalImported = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line;

            while ((line = reader.readLine()) != null) {

                try {

                    String[] p = line.split("\t");

                    // GeoNames requires 12 fields
                    if (p.length < 12) {
                        continue;
                    }

                    String countryCode = p[0];
                    String zip = p[1];
                    String cityName = p[2];
                    String stateName = p[3];

                    if (!"DE".equals(countryCode)) {
                        continue;
                    }

                    if (zip.isBlank() || cityName.isBlank()) {
                        continue;
                    }

                    // -------------------------
                    // Coordinates
                    // -------------------------
                    Double lat = null;
                    Double lon = null;

                    if (!p[9].isBlank()) {
                        lat = Double.parseDouble(p[9]);
                    }

                    if (!p[10].isBlank()) {
                        lon = Double.parseDouble(p[10]);
                    }

                    Integer accuracy = null;
                    if (!p[11].isBlank()) {
                        accuracy = Integer.parseInt(p[11]);
                    }

                    // -------------------------
                    // STATE
                    // -------------------------
                    State state = stateRepository
                            .findByCountryAndName(germany, stateName)
                            .orElseGet(() -> {
                                State s = new State();
                                s.setCountry(germany);
                                s.setName(stateName);
                                return stateRepository.save(s);
                            });

                    // -------------------------
                    // CITY
                    // -------------------------
                    String cityKey = stateName + "_" + cityName;

                    City city = cityRepository
                            .findByStateAndName(state, cityName)
                            .orElseGet(() -> {
                                City c = new City();
                                c.setState(state);
                                c.setName(cityName);
                                return cityRepository.save(c);
                            });


                    // -------------------------
                    // POSTAL CODE
                    // -------------------------
                    PostalCode postal = new PostalCode();
                    postal.setCity(city);
                    postal.setZip(zip);
                    postal.setAccuracy(accuracy);

                    // Spatial point only if valid
                    if (lat != null && lon != null) {
                        Point point = geometryFactory.createPoint(
                                new Coordinate(lon, lat) // lon first!
                        );
                        postal.setLocation(point);
                    }

                    entityManager.persist(postal);

                    totalImported++;

                    // -------------------------
                    // BATCH FLUSH
                    // -------------------------
                    if (++batchCounter % BATCH_SIZE == 0) {

                        entityManager.flush();
                        entityManager.clear();

                        log.info("Imported {} records so far...", totalImported);
                    }

                } catch (Exception e) {

                    log.warn("Skipping invalid line: {}", line);
                }
            }

            entityManager.flush();
            entityManager.clear();
        }

        log.info("Germany import completed. Total imported: {}", totalImported);
    }
}
