package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.Timezone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TimezoneRepository extends JpaRepository<Timezone, UUID> {
    Optional<Timezone> findByZoneName(String zoneName);
}
