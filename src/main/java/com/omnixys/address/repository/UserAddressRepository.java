package com.omnixys.address.repository;

import com.omnixys.address.models.entity.UserAddress;
import com.omnixys.address.models.payload.UserAddressPayload;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID>, JpaSpecificationExecutor<UserAddress> {

    void deleteByUserId(UUID userId);

    @Override
    @NonNull
    Optional<UserAddress> findById(UUID id);

    @Query("""
SELECT new com.omnixys.address.models.payload.UserAddressPayload(
    ua.id,
    ua.userId,
    c.name,
    s.name,
    ci.name,
    pc.code,
    st.name,
    hn.number,
    ua.additionalInfo,
    ua.addressType
)
FROM UserAddress ua
LEFT JOIN Country c ON c.id = ua.countryId
LEFT JOIN State s ON s.id = ua.stateId
LEFT JOIN City ci ON ci.id = ua.cityId
LEFT JOIN PostalCode pc ON pc.id = ua.postalCodeId
LEFT JOIN Street st ON st.id = ua.streetId
LEFT JOIN HouseNumber hn ON hn.id = ua.houseNumberId
WHERE ua.userId = :userId
""")
    List<UserAddressPayload> findPayloadByUserId(UUID userId);

}
