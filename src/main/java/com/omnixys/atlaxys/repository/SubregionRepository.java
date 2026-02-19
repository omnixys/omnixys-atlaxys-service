package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.Continent;
import com.omnixys.atlaxys.models.entity.Subregion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubregionRepository extends JpaRepository<Subregion, UUID> {
    Optional<Subregion> findByNameAndContinent(String name, Continent continent);
}
