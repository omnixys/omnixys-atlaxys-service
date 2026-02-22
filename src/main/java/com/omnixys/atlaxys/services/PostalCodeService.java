package com.omnixys.atlaxys.services;

import com.omnixys.atlaxys.models.entity.PostalCode;
import com.omnixys.atlaxys.repository.PostalCodeRepository;
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


    // -----------------------------------
    // findById
    // -----------------------------------
    public PostalCode findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("PostalCode not found: " + id));
    }

    public List<PostalCode> findByZip(String zip) {

        log.debug("Searching PostalCode by zip={}", zip);

        var postalCodes =  repository.findByZipIgnoreCase(zip);
   if(postalCodes.isEmpty()) {
                    log.warn("PostalCode not found for zip={}", zip);
                    throw new IllegalArgumentException("PostalCode not found: " + zip);
                }
   return postalCodes;
    }

    // -----------------------------------
    // find (optional filter)
    // -----------------------------------
    public Page<PostalCode> find(
            UUID countryId,
            UUID cityId,
            String zip,
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

            if (zip != null && !zip.isBlank()) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("zip")),
                                "%" + zip.toLowerCase() + "%"
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