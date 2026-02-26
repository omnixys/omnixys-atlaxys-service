package com.omnixys.address.models.inputs;

import java.util.UUID;

public record PostalCodeFilterInput(
        UUID countryId,
        UUID cityId,
        String zip
) {}
