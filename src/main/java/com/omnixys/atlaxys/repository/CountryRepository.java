package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.Country;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID>, JpaSpecificationExecutor<Country> {
    Optional<Country> findByIso2(String iso2);
    Optional<Country> findByIso3(String code);

    @EntityGraph(attributePaths = {
            "continent",
            "subregion",
            "currency",
            "callingCode",
            "languages",
            "timezones"
    })
    Optional<Country> findByIso2IgnoreCase(String iso2);
    Optional<Country> findByIso3IgnoreCase(String iso3);
    Optional<Country> findByName(String name);
}
