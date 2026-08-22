package com.omnixys.address.repository;

import com.omnixys.address.models.entitys.UserAddress;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID>, JpaSpecificationExecutor<UserAddress> {

    void deleteByUserId(UUID userId);

    List<UserAddress> findByUserId(UUID userId);

    @Override
    @NonNull
    Optional<UserAddress> findById(UUID id);

}
