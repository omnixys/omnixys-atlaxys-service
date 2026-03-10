package com.omnixys.address.repository;

import com.omnixys.address.models.entity.City;
import com.omnixys.address.models.entity.PostalCode;
import com.omnixys.address.models.entity.State;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
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

    @NonNull
    Optional<PostalCode> findByCodeAndCityId(final String code, final UUID cityId);

    boolean existsByCityAndCode(City city, String code);
    List<PostalCode> findByCityId(UUID cityId);
    Page<PostalCode> findByCity_Id(UUID cityId, Pageable pageable);

    @EntityGraph(attributePaths = {
            "city"
    })
    @Override
    @NonNull
    Optional<PostalCode> findById(UUID id);

    @EntityGraph(attributePaths = {
            "city"
    })
    List<PostalCode> findByCodeIgnoreCase(String code);


    @Query("""
            select p
            from PostalCode p
            where lower(p.country.iso2) = lower(:iso2)
              and p.code = :code
            """)
    List<PostalCode> findAllByCountryIso2AndCode(@Param("iso2") String iso2,
                                                @Param("code") String code);
}
