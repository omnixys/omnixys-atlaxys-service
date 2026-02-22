package com.omnixys.atlaxys.repository;

import com.omnixys.atlaxys.models.entity.City;
import com.omnixys.atlaxys.models.entity.State;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends JpaRepository<City, UUID>, JpaSpecificationExecutor<City> {
    Optional<City> findByNameIgnoreCaseAndState_Country_Id(
            String name,
            UUID countryId
    );

    @EntityGraph(attributePaths = {
            "state"
    })
    List<City> findByStateId(UUID stateId);

    /**
     * Best-effort resolver for a city based on Geoapify fields.
     * Adjust joins/fields to match your schema (country/state relations, iso2, etc.).
     */
    @Query("""
            select c
            from City c
            where lower(c.name) = lower(:city)
            """)
    Optional<City> findBestMatch(@Param("city") String city,
                                 @Param("state") String state,
                                 @Param("country") String country,
                                 @Param("countryCode") String countryCode,
                                 @Param("postalCode") String postalCode);

    @Query("""
            select c
            from City c
            where lower(c.name) = lower(:cityName)
              and lower(c.state.name) = lower(:stateName)
              and lower(c.state.country.iso2) = lower(:countryIso2)
            """)
    Optional<City> findByCityStateCountry(@Param("cityName") String cityName,
                                          @Param("stateName") String stateName,
                                          @Param("countryIso2") String countryIso2);

}
