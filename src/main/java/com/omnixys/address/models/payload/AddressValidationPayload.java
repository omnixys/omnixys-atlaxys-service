package com.omnixys.address.models.payload;

public record AddressValidationPayload(
        boolean valid,
        String reason,
        Double confidence,
        String formatted,
        Double lon,
        Double lat
) {}
