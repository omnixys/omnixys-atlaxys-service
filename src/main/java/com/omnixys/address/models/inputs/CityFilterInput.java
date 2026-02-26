package com.omnixys.address.models.inputs;

import java.util.UUID;

public record CityFilterInput(
        UUID stateId,
        String name,
        String type,
        Long minPopulation,
        Long maxPopulation
) {}