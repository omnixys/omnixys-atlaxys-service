package com.omnixys.address.services;

import com.omnixys.address.models.entity.PostalCode;
import com.omnixys.address.models.entity.State;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostalCodeService {

    private final PostalCodeRepository repository;

    public PostalCode findByCodeAndCityId(final String name, final UUID cityId) {
        log.debug("Fetching state by name={} and city={}", name, cityId);

        return repository.findByCodeAndCityId(name, cityId)
                .orElseThrow(() -> {
                    log.warn("PostalCode not found for code={} and city={}", name, cityId);
                    return new IllegalArgumentException("PostalCode not found: " + name);
                });
    }

    public PostalCode findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PostalCode not found: " + id));
    }

    public List<PostalCode> findByCode(String code) {

        log.debug("Searching PostalCode by code={}", code);

        var postalCodes =  repository.findByCodeIgnoreCase(code);
   if(postalCodes.isEmpty()) {
                    log.warn("PostalCode not found for code={}", code);
                    throw new IllegalArgumentException("PostalCode not found: " + code);
                }
   return postalCodes;
    }

    // -----------------------------------
    // find (optional filter)
    // -----------------------------------
    public Page<PostalCode> find(
            UUID countryId,
            UUID cityId,
            String code,
            Pageable pageable
    ) {

        Specification<PostalCode> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (countryId != null) {
                predicates.add(cb.equal(root.get("country").get("id"), countryId));
            }

            if (cityId != null) {
                predicates.add(cb.equal(root.get("city").get("id"), cityId));
            }

            if (code != null && !code.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("code")),
                                "%" + code.toLowerCase() + "%"
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, pageable);
    }

    // -----------------------------------
    // findByState
    // -----------------------------------
    public List<PostalCode> findByState(UUID stateId) {

        Specification<PostalCode> spec = (root, query, cb) ->
                cb.equal(root.get("city").get("state").get("id"), stateId);

        return repository.findAll(spec);
    }


    // -----------------------------------
    // findByCity (List)
    // -----------------------------------
    public List<PostalCode> findByCityId(UUID cityId) {
        if (cityId == null) {
            return List.of();
        }
        return repository.findByCityId(cityId);
    }

    // -----------------------------------
    // findByCity (Page)
    // -----------------------------------
    public Page<PostalCode> findByCity(UUID cityId, Pageable pageable) {
        if (cityId == null) {
            return Page.empty(pageable);
        }
        return repository.findByCity_Id(cityId, pageable);
    }

}