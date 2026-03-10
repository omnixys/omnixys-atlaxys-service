package com.omnixys.address.services;

import com.omnixys.address.models.entity.City;
import com.omnixys.address.models.entity.PostalCode;
import com.omnixys.address.models.entity.State;
import com.omnixys.address.repository.CityRepository;
import com.omnixys.address.repository.PostalCodeRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityService {

    private final CityRepository repository;
    private final PostalCodeService  postalCodeService;


    public City findByNameAndStateId(final String name, final UUID stateId) {
        log.debug("Fetching city by name={} and stateId={}", name, stateId);

        return repository.findByNameAndStateId(name, stateId)
                .orElseThrow(() -> {
                    log.warn("city not found for name={} and stateId={}", name, stateId);
                    return new IllegalArgumentException("city not found: " + name);
                });
    }


    public City findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("City not found: " + id));
    }

    // -----------------------------------
    // find (optional filter)
    // -----------------------------------
    public Page<City> find(
            UUID stateId,
            String name,
            String type,
            Long minPopulation,
            Long maxPopulation,
            Pageable pageable
    ) {

        Specification<City> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (stateId != null) {
                predicates.add(cb.equal(root.get("state").get("id"), stateId));
            }

            if (name != null && !name.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%"
                        )
                );
            }

            if (type != null && !type.isBlank()) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (minPopulation != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("population"), minPopulation));
            }

            if (maxPopulation != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("population"), maxPopulation));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable);
    }

    // -----------------------------------
    // findByState
    // -----------------------------------
    public List<City> findByState(UUID stateId) {
        log.debug("fetching Cities of StateId={}", stateId);

        var cities = repository.findByStateId(stateId);
        log.debug("fetched {} cities of state {}", cities.size(), cities.getFirst().getState().getName());

        return cities;
    }


    // -----------------------------------
    // Find by code String
    // -----------------------------------
    public List<City> findByPostalCode(String code) {

        log.debug("findByPostalCode(String) called with code={}", code);

        var postalCodes = postalCodeService.findByCode(code);

        if (postalCodes.isEmpty()) {
            log.warn("No postal codes found for code={}", code);
            return null;
        }

        var cities = postalCodes.stream()
                .map(PostalCode::getCity)
                .toList();

        log.debug("Resolved {} cities for code={}", cities.size(), code);
        return cities;
    }

    // -----------------------------------
    // Find by PostalCode ID
    // -----------------------------------
    public City findByPostalCode(UUID postalCodeId) {

        log.debug("findByPostalCode(UUID) called with postalCodeId={}", postalCodeId);

        var postalCode = postalCodeService.findById(postalCodeId);

        var city = postalCode.getCity();

        log.debug("Resolved city={} for postalCodeId={}",
                city != null ? city.getName() : null,
                postalCodeId
        );

        return city;
    }
}