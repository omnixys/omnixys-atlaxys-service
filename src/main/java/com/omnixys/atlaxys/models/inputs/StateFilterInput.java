package com.omnixys.atlaxys.models.inputs;

import java.util.UUID;

public record StateFilterInput(
        String name,
        String iso3166_2,
        String code,
        String type,

        UUID countryId,
        String countryIso2,
        String countryIso3
) {}