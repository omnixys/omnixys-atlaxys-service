package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.City;
import com.omnixys.atlaxys.models.entity.PostalCode;
import com.omnixys.atlaxys.models.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PostalCodeRepository extends JpaRepository<PostalCode, UUID>, JpaSpecificationExecutor<PostalCode> {
    boolean existsByCityAndZip(City city, String zip);
}
