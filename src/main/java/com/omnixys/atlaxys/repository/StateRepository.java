package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.Country;
import com.omnixys.atlaxys.models.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StateRepository extends JpaRepository<State, UUID>, JpaSpecificationExecutor<State> {
    Optional<State> findByCountryAndCode(Country country, String code);
    List<State> findByCountry(Country country);
}
