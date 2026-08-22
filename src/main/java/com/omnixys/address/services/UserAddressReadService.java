package com.omnixys.address.services;

import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.inputs.UserAddressFilter;
import com.omnixys.address.models.mappers.UserAddressMapper;
import com.omnixys.address.models.payloads.UserAddressPayload;
import com.omnixys.address.repository.UserAddressRepository;
import com.omnixys.address.repository.UserAddressSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAddressReadService {

    private final UserAddressRepository repository;
    private final UserAddressMapper mapper;

    public List<UserAddressPayload> findByUserId(UUID userId) {
        log.debug("Fetching addresses for userId={}", userId);

        return repository.findByUserId(userId)
                .stream()
                .map(mapper::toPayload)
                .toList();
    }

    public Optional<UserAddressPayload> findById(UUID id) {
        log.debug("Fetching address id={}", id);

        return repository.findById(id)
                .map(mapper::toPayload);
    }

    public List<UserAddressPayload> find(UserAddressFilter filter) {
        Specification<UserAddress> spec =
                UserAddressSpecificationBuilder.build(filter);

        return repository.findAll(spec)
                .stream()
                .map(mapper::toPayload)
                .toList();
    }
}
