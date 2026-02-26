package com.omnixys.address.repository;

import com.omnixys.address.models.entity.HouseNumber;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseNumberRepository extends JpaRepository<HouseNumber, UUID> {

    // ---------- Exact lookups ----------

    @Query("""
            select h
            from HouseNumber h
            where h.street.id = :streetId
              and lower(h.number) = lower(:number)
            """)
    Optional<HouseNumber> findByStreetIdAndNumberIgnoreCase(UUID streetId,String number);

    boolean existsByStreet_Id(UUID streetId);

    // ---------- Autocomplete / Search ----------

    /**
     * Prefix search for house numbers on a given street (e.g., "12", "12A").
     */
    @Query(value = """
            select *
            from address.house_number h
            where h.street_id = :streetId
              and h.number ilike concat(:prefix, '%')
            order by h.number asc
            """, nativeQuery = true)
    List<HouseNumber> autocompleteByStreet(@Param("streetId") UUID streetId,
                                           @Param("prefix") String prefix,
                                           Pageable pageable);

    /**
     * (Optional) Contains search, usually not needed, but useful for very permissive input.
     */
    @Query(value = """
            select *
            from address.house_number h
            where h.street_id = :streetId
              and h.number ilike concat('%', :q, '%')
            order by h.number asc
            """, nativeQuery = true)
    List<HouseNumber> searchByStreet(@Param("streetId") UUID streetId,
                                     @Param("q") String q,
                                     Pageable pageable);

    // ---------- Geo ----------

    /**
     * Returns house numbers within a radius (meters) around given lon/lat.
     */
    @Query(value = """
            select *
            from address.house_number h
            where h.location is not null
              and ST_DWithin(
                    h.location,
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                    :radiusMeters
              )
            order by ST_Distance(
                    h.location,
                    ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
            ) asc
            """, nativeQuery = true)
    List<HouseNumber> findWithinRadius(@Param("lon") double lon,
                                       @Param("lat") double lat,
                                       @Param("radiusMeters") double radiusMeters,
                                       Pageable pageable);

    /**
     * Useful: get all house numbers for a street (paged).
     */
    @Query("""
            select h
            from HouseNumber h
            where h.street.id = :streetId
            order by h.number asc
            """)
    List<HouseNumber> findAllByStreetId(@Param("streetId") UUID streetId, Pageable pageable);
}