package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LanguageRepository extends JpaRepository<Language, UUID> {
    Optional<Language> findByIso2(String iso2);
    Optional<Language> findByIso3(String iso3);
}
