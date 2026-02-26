package com.omnixys.address.repository;

import com.omnixys.address.models.entity.City;
import com.omnixys.address.models.entity.Street;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StreetRepository extends JpaRepository<Street, UUID> {


    @Query("""
            select s
            from Street s
            where s.city.id = :cityId
              and lower(s.name) = lower(:name)
            """)
    Optional<Street> findByCityIdAndNameIgnoreCase(@Param("cityId") UUID cityId,
                                                   @Param("name") String name);
    boolean existsByCity_Id(UUID cityId);

    // ---------- Autocomplete / Search ----------

    /**
     * Prefix search (fast for autocomplete). Uses ILIKE so it remains DB-side and index-friendly with pg_trgm.
     */
    @Query(value = """
            select *
            from address.street s
            where s.city_id = :cityId
              and s.name ilike concat(:prefix, '%')
            order by s.name asc
            """, nativeQuery = true)
    List<Street> autocompleteByCity(@Param("cityId") UUID cityId,
                                    @Param("prefix") String prefix,
                                    Pageable pageable);

    /**
     * Fuzzy contains search. Trigram index can help for larger datasets.
     */
    @Query(value = """
            select *
            from address.street s
            where s.city_id = :cityId
              and s.name ilike concat('%', :q, '%')
            order by s.name asc
            """, nativeQuery = true)
    List<Street> searchByCity(@Param("cityId") UUID cityId,
                              @Param("q") String q,
                              Pageable pageable);

    // ---------- Geo ----------

    /**
     * Returns streets within a radius (meters) around the given WGS84 lon/lat.
     * Uses geography distance, so meters are accurate enough globally.
     */
    @Query(value = """
            select *
            from address.street s
            where s.location is not null
              and ST_DWithin(
                    s.location,
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                    :radiusMeters
              )
            order by ST_Distance(
                    s.location,
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            ) asc
            """, nativeQuery = true)
    List<Street> findWithinRadius(@Param("lon") double lon,
                                  @Param("lat") double lat,
                                  @Param("radiusMeters") double radiusMeters,
                                  Pageable pageable);
}
