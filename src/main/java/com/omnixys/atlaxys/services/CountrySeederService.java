package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.dto.CountryDTO;
import com.omnixys.atlaxys.models.entity.*;
import com.omnixys.atlaxys.models.entity.Currency;
import com.omnixys.atlaxys.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CountrySeederService {

    private final CountryRepository countryRepository;
    private final ContinentRepository continentRepository;
    private final SubregionRepository subregionRepository;
    private final CurrencyRepository currencyRepository;
    private final LanguageRepository languageRepository;
    private final TimezoneRepository timezoneRepository;
    private final CallingCodeRepository callingCodeRepository;

    @PersistenceContext
    private EntityManager em;

    private final RestClient restClient = RestClient.create();

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    private static final int BATCH_SIZE = 100;

    public void seedCountries() {

        if (!seedEnabled) {
            throw new IllegalStateException("Seeding disabled");
        }

        List<CountryDTO> apiCountries =
                restClient.get()
                        .uri("https://www.apicountries.com/countries")
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

        if (apiCountries == null || apiCountries.isEmpty()) return;

        Map<String, Country> existingCountries =
                countryRepository.findAll()
                        .stream()
                        .collect(HashMap::new,
                                (m, c) -> m.put(c.getIso2(), c),
                                HashMap::putAll);

        int counter = 0;

        for (CountryDTO api : apiCountries) {

            if (api == null || api.alpha2Code() == null) continue;

            Country country =
                    existingCountries.getOrDefault(
                            api.alpha2Code(),
                            new Country()
                    );

            if (country.getId() == null) {
                country.setIso2(api.alpha2Code());
            }

            applyScalar(country, api);

            country.setContinent(resolveContinent(api));
            country.setSubregion(resolveSubregion(api, country.getContinent()));

            syncRelations(country, api);

            em.merge(country); // safe for new + existing

            if (++counter % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }

        em.flush();
        em.clear();
    }

    // =========================
    // Scalar
    // =========================

    private void applyScalar(Country country, CountryDTO api) {
        country.setName(api.name());
        country.setIso3(api.alpha3Code());
        country.setNativeName(api.nativeName());

        if (api.flags() != null) {
            country.setFlagSvg(api.flags().svg());
            country.setFlagPng(api.flags().png());
        }
    }

    // =========================
    // Reference Resolution
    // =========================

    private Continent resolveContinent(CountryDTO api) {

        String name = Optional.ofNullable(api.region()).orElse("Unknown");

        return continentRepository.findByName(name)
                .orElseGet(() ->
                        continentRepository.save(
                                Continent.builder().name(name).build()
                        )
                );
    }

    private Subregion resolveSubregion(CountryDTO api, Continent continent) {

        String name = Optional.ofNullable(api.subregion()).orElse("Unknown");

        return subregionRepository
                .findByNameAndContinent(name, continent)
                .orElseGet(() ->
                        subregionRepository.save(
                                Subregion.builder()
                                        .name(name)
                                        .continent(continent)
                                        .build()
                        )
                );
    }

    // =========================
    // RELATIONS
    // =========================

    private void syncRelations(Country country, CountryDTO api) {
        syncCurrencies(country, api);
        syncLanguages(country, api);
        syncTimezones(country, api);
        syncCallingCodes(country, api);
    }

    private void syncCurrencies(Country country, CountryDTO api) {

        if (api.currencies() == null) return;

        Set<Currency> desired = new HashSet<>();

        for (CountryDTO.CurrencyDTO c : api.currencies()) {

            if (c == null) continue;

            String code = trimUpper(c.code());

            if (code == null || !code.matches("^[A-Z]{3}$")) continue;

            Currency currency =
                    currencyRepository.findByCode(code)
                            .orElseGet(() ->
                                    currencyRepository.save(
                                            Currency.builder()
                                                    .code(code)
                                                    .name(nullToEmpty(c.name()))
                                                    .symbol(nullToEmpty(c.symbol()))
                                                    .build()
                                    )
                            );

            desired.add(currency);
        }

        country.getCurrencies().retainAll(desired);
        country.getCurrencies().addAll(desired);
    }

    private void syncLanguages(Country country, CountryDTO api) {

        if (api.languages() == null) return;

        Set<Language> desired = new HashSet<>();

        for (CountryDTO.LanguageDTO l : api.languages()) {

            if (l == null) continue;

            String iso2 = trimUpper(l.iso639_1());

            if (iso2 == null) continue;

            Language language =
                    languageRepository.findByIso2(iso2)
                            .orElseGet(() ->
                                    languageRepository.save(
                                            Language.builder()
                                                    .iso2(iso2)
                                                    .iso3(trimUpper(l.iso639_2()))
                                                    .name(l.name())
                                                    .build()
                                    )
                            );

            desired.add(language);
        }

        country.getLanguages().retainAll(desired);
        country.getLanguages().addAll(desired);
    }

    private void syncTimezones(Country country, CountryDTO api) {

        if (api.timezones() == null) return;

        Set<Timezone> desired = new HashSet<>();

        for (String tz : api.timezones()) {

            if (tz == null) continue;

            Timezone timezone =
                    timezoneRepository.findByUtcOffset(tz)
                            .orElseGet(() ->
                                    timezoneRepository.save(
                                            Timezone.builder()
                                                    .utcOffset(tz)
                                                    .build()
                                    )
                            );

            desired.add(timezone);
        }

        country.getTimezones().retainAll(desired);
        country.getTimezones().addAll(desired);
    }

    private void syncCallingCodes(Country country, CountryDTO api) {

        if (api.callingCodes() == null) return;

        Set<CallingCode> desired = new HashSet<>();

        for (String cc : api.callingCodes()) {

            if (cc == null) continue;

            CallingCode callingCode =
                    callingCodeRepository.findByCode(cc)
                            .orElseGet(() ->
                                    callingCodeRepository.save(
                                            CallingCode.builder()
                                                    .code(cc)
                                                    .build()
                                    )
                            );

            desired.add(callingCode);
        }

        country.getCallingCodes().retainAll(desired);
        country.getCallingCodes().addAll(desired);
    }

    private static String trimUpper(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t.toUpperCase();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
