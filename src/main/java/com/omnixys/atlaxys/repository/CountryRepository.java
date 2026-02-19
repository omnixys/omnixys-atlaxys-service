package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<Country, UUID>, JpaSpecificationExecutor<Country> {
    Optional<Country> findByIso2(String iso2);
}
