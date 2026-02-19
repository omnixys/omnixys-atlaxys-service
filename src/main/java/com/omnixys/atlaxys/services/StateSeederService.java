package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.dto.StateApiResponse;
import com.omnixys.atlaxys.models.entity.Country;
import com.omnixys.atlaxys.models.entity.State;
import com.omnixys.atlaxys.repository.CountryRepository;
import com.omnixys.atlaxys.repository.StateRepository;
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
public class StateSeederService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;
    private final RestClient restClient = RestClient.create();

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.states.api.key}")
    private String apiKey;

    public void seedStates() {

        if (!seedEnabled) {
            throw new IllegalStateException("Seeding disabled");
        }

        List<Country> countries = countryRepository.findAll();

        for (Country country : countries) {

            List<StateApiResponse> states =
                    restClient.get()
                            .uri("https://api.countrystatecity.in/v1/countries/{country}/states",
                                    country.getIso2())
                            .header("X-CSCAPI-KEY", apiKey)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});

            if (states == null) continue;

            for (StateApiResponse apiState : states) {

                stateRepository
                        .findByCountryAndName(country, apiState.name())
                        .ifPresentOrElse(
                                existing -> update(existing, apiState),
                                () -> insert(country, apiState)
                        );
            }
        }
    }

    private void insert(Country country, StateApiResponse api) {
        State state = new State();
        state.setCountry(country);
        state.setName(api.name());
        stateRepository.save(state);
    }

    private void update(State existing, StateApiResponse api) {
        existing.setName(api.name());
    }
}

