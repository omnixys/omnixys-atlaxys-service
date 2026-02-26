package com.omnixys.address.repository;

import com.omnixys.address.models.entity.Continent;
import com.omnixys.address.models.entity.Subregion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubregionRepository extends JpaRepository<Subregion, UUID> {
    Optional<Subregion> findByNameAndContinent(String name, Continent continent);
}
