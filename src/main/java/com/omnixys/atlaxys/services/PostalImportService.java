package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.dto.StateApiResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostalImportService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final PostalCodeRepository postalRepository;
    private final EntityManager entityManager;

    private static final int BATCH_SIZE = 1000;

    public void importZipFile(Path zipFilePath) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(zipFilePath)) {

            String line;
            int counter = 0;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\t");

                if (parts.length < 10) continue;

                String countryCode = parts[0];
                String postalCode = parts[1];
                String cityName = parts[2];
                String stateName = parts[3];
                double latitude = Double.parseDouble(parts[8]);
                double longitude = Double.parseDouble(parts[9]);

                processRow(
                        countryCode,
                        stateName,
                        cityName,
                        postalCode,
                        latitude,
                        longitude
                );

                if (++counter % BATCH_SIZE == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        }
    }

    private void processRow(
            String countryCode,
            String stateName,
            String cityName,
            String postal,
            double lat,
            double lon
    ) {

        Country country = countryRepository
                .findByIso2(countryCode)
                .orElseThrow();

        State state = stateRepository
                .findByCountryAndName(country, stateName)
                .orElseGet(() -> {
                    State s = new State();
                    s.setCountry(country);
                    s.setName(stateName);
                    return stateRepository.save(s);
                });

        City city = cityRepository
                .findByStateAndName(state, cityName)
                .orElseGet(() -> {
                    City c = new City();
                    c.setState(state);
                    c.setName(cityName);
                    return cityRepository.save(c);
                });

        if (!postalRepository.existsByCityAndZip(city, postal)) {

            PostalCode pc = new PostalCode();
            pc.setCity(city);
            pc.setZip(postal);
            pc.setLatitude(BigDecimal.valueOf(lat));
            pc.setLongitude(BigDecimal.valueOf(lon));

            postalRepository.save(pc);
        }
    }
}

