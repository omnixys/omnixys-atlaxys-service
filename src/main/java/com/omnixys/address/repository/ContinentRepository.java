package com.omnixys.address.repository;

import com.omnixys.address.models.entity.Continent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContinentRepository extends JpaRepository<Continent, UUID> {
    Optional<Continent> findByName(String name);
    Optional<Continent> findByCode(String code);
}
