package com.omnixys.atlaxys.models.inputs;

import java.util.UUID;

public record PostalCodeFilterInput(
        UUID countryId,
        UUID cityId,
        String zip
) {}
