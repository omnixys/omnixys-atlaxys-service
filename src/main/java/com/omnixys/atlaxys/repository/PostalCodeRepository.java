package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.City;
import com.omnixys.atlaxys.models.entity.PostalCode;
import com.omnixys.atlaxys.models.entity.State;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostalCodeRepository extends JpaRepository<PostalCode, UUID>, JpaSpecificationExecutor<PostalCode> {
    boolean existsByCityAndZip(City city, String zip);
    List<PostalCode> findByCityId(UUID cityId);
    Page<PostalCode> findByCity_Id(UUID cityId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "city"
    })
    @Override
    Optional<PostalCode> findById(UUID id);

    @EntityGraph(attributePaths = {
            "city"
    })
    List<PostalCode> findByZipIgnoreCase(String zip);


    @Query("""
            select p
            from PostalCode p
            where lower(p.country.iso2) = lower(:iso2)
              and p.zip = :zip
            """)
    List<PostalCode> findAllByCountryIso2AndZip(@Param("iso2") String iso2,
                                                @Param("zip") String zip);
}
