package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.City;
import com.omnixys.atlaxys.models.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID>, JpaSpecificationExecutor<City> {
    Optional<City> findByStateAndName(State state, String name);

}
