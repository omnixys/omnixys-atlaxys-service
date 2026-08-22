package com.omnixys.address.repository;

import com.omnixys.address.models.entitys.EventAddress;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventAddressRepository extends JpaRepository<EventAddress, UUID>, JpaSpecificationExecutor<EventAddress> {

    void deleteByEventId(UUID eventId);

    @Override
    @NonNull
    Optional<EventAddress> findById(UUID id);

    /**
     * Native query because PostGIS functions are required.
     */
    String PROJECTION_SELECT = """
        SELECT
            ea.id AS id,
            ea.event_id AS "eventId",
            c.name AS country,
            s.name AS state,
            ci.name AS city,
            pc.code AS "postalCode",
            st.name AS street,
            hn.number AS "houseNumber",
            ea.additional_info AS "additionalInfo",
            ST_Y(COALESCE(hn.location, st.location)::geometry) AS lat,
            ST_X(COALESCE(hn.location, st.location)::geometry) AS lon
        FROM event_address ea
        LEFT JOIN country c ON c.id = ea.country_id
        LEFT JOIN state s ON s.id = ea.state_id
        LEFT JOIN city ci ON ci.id = ea.city_id
        LEFT JOIN postal_code pc ON pc.id = ea.postal_code_id
        LEFT JOIN street st ON st.id = ea.street_id
        LEFT JOIN house_number hn ON hn.id = ea.house_number_id
        """;

    @Query(value = PROJECTION_SELECT + "WHERE ea.id = :id\nLIMIT 1", nativeQuery = true)
    Optional<EventAddressProjection> findProjectedById(UUID id);

    @Query(value = PROJECTION_SELECT + "WHERE ea.event_id = :eventId\nLIMIT 1", nativeQuery = true)
    Optional<EventAddressProjection> findProjectedByEventId(UUID eventId);
}
