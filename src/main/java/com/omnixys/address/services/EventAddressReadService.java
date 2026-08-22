package com.omnixys.address.services;

import com.omnixys.address.errors.AddressNotFoundException;
import com.omnixys.address.models.mappers.EventAddressMapper;
import com.omnixys.address.models.payloads.EventAddressPayload;
import com.omnixys.address.repository.EventAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventAddressReadService {

    private final EventAddressRepository repository;
    private final EventAddressMapper mapper;

    public EventAddressPayload findById(UUID id) {

        log.debug("Fetching event address by id={}", id);

        return repository.findProjectedById(id)
                .map(mapper::toPayload)
                .orElseThrow(() -> new AddressNotFoundException(id));
    }

    public EventAddressPayload findByEventId(UUID eventId) {

        log.debug("Fetching event address by eventId={}", eventId);

        return repository.findProjectedByEventId(eventId)
                .map(mapper::toPayload)
                .orElseThrow(() -> new AddressNotFoundException(eventId));
    }
}
