package com.omnixys.address.repository;

import com.omnixys.address.models.entity.CallingCode;
import com.omnixys.address.models.entity.Continent;
import com.omnixys.address.models.entity.Subregion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CallingCodeRepository extends JpaRepository<CallingCode, UUID> {
    Optional<CallingCode> findByCode(String code);
}

