package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.Country;
import com.omnixys.atlaxys.models.dto.CountryApiResponse;
import com.omnixys.atlaxys.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CountrySeederService {

    private final CountryRepository repository;
    private final RestClient restClient = RestClient.create();

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    public void seedCountries() {

        if (!seedEnabled) {
            throw new IllegalStateException("Seeding disabled by configuration");
        }

        List<CountryApiResponse> countries =
                restClient.get()
                        .uri("https://www.apicountries.com/countries")
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        for (CountryApiResponse apiCountry : countries) {

            repository.findByIso2(apiCountry.alpha2Code())
                    .ifPresentOrElse(
                            existing -> update(existing, apiCountry),
                            () -> insert(apiCountry)
                    );
        }
    }

    private void insert(CountryApiResponse api) {
        Country country = new Country();
        country.setName(api.name());
        country.setIso2(api.alpha2Code());
        country.setIso3(api.alpha3Code());
        country.setFlagSvg(api.flags().svg());
        country.setFlagPng(api.flags().png());

        repository.save(country);
    }

    private void update(Country existing, CountryApiResponse api) {
        existing.setName(api.name());
        existing.setIso3(api.alpha3Code());
        existing.setFlagSvg(api.flags().svg());
        existing.setFlagPng(api.flags().png());
    }
}

