package com.omnixys.address.services;

import com.omnixys.address.models.entity.HouseNumber;
import com.omnixys.address.repository.HouseNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class HouseNumberService {
    public final HouseNumberRepository houseNumberRepository;

    public HouseNumber findByHouseNumberAndStreetId(final String houseNumber, final UUID streetId) {
        log.debug("Fetching state by houseNumber={}", houseNumber);

        return houseNumberRepository.findByNumberAndStreetId(houseNumber, streetId)
                .orElseThrow(() -> {
                    log.warn("HouseNumber not found for houseNumber={}", houseNumber);
                    return new IllegalArgumentException("HouseNumber not found: " + houseNumber);
                });
    }
    
    public HouseNumber findById(UUID id) {
        log.debug("Fetching houseNumber by id={}", id);

        return houseNumberRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("HouseNumber not found for id={}", id);
                    return new IllegalArgumentException("HouseNumber not found: " + id);
                });
    }
}

