package com.omnixys.address.services;

import com.omnixys.address.models.entity.State;
import com.omnixys.address.models.entity.Street;
import com.omnixys.address.repository.StreetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class StreetService {
    public final StreetRepository streetRepository;

    public Street findByNameAndCityId(final String name, final UUID cityId) {
        log.debug("Fetching state by name={} and cityId={}", name, cityId);

        return streetRepository.findByNameAndCityId(name, cityId)
                .orElseThrow(() -> {
                    log.warn("Street not found for name={}", name);
                    return new IllegalArgumentException("Street not found: " + name);
                });
    }

    public Street findById(UUID id) {
        log.debug("Fetching street by id={}", id);

        return streetRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Street not found for id={}", id);
                    return new IllegalArgumentException("Street not found: " + id);
                });
    }
}

