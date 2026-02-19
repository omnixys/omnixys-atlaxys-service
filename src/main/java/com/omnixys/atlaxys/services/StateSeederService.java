package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.dto.StateDTO;
import com.omnixys.atlaxys.models.entity.Country;
import com.omnixys.atlaxys.models.entity.State;
import com.omnixys.atlaxys.repository.CountryRepository;
import com.omnixys.atlaxys.repository.StateRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StateSeederService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    @PersistenceContext
    private EntityManager em;

    private final RestClient restClient = RestClient.create();

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Value("${app.states.api.key}")
    private String apiKey;

    private static final int BATCH_SIZE = 200;

    public void seedStates() {

        if (!seedEnabled) {
            throw new IllegalStateException("Seeding disabled");
        }

        List<Country> countries = countryRepository.findAll();
        int counter = 0;

        for (Country country : countries) {

            List<StateDTO> apiStates =
                    restClient.get()
                            .uri("https://api.countrystatecity.in/v1/countries/{country}/states",
                                    country.getIso2())
                            .header("X-CSCAPI-KEY", apiKey)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});

            if (apiStates == null || apiStates.isEmpty()) continue;

            // Bulk preload existing states
            Map<String, State> existingStates =
                    stateRepository.findByCountry(country)
                            .stream()
                            .collect(Collectors.toMap(
                                    s -> s.getCode().toUpperCase(),
                                    s -> s
                            ));

            for (StateDTO api : apiStates) {

                if (api == null) continue;

                String code = normalize(api.iso2());
                String name = normalize(api.name());

                if (code == null || name == null) continue;

                State state = existingStates.get(code);

                if (state == null) {
                    state = State.builder()
                            .code(code)
                            .name(name)
                            .country(country)
                            .build();

                    em.persist(state);
                    existingStates.put(code, state);
                } else {
                    state.setName(name);
                }

                if (++counter % BATCH_SIZE == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }

        em.flush();
        em.clear();
    }

    private static String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.toUpperCase();
    }
}
