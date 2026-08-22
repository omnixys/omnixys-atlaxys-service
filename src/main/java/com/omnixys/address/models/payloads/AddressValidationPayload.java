package com.omnixys.address.models.payloads;

public record AddressValidationPayload(
        boolean valid,
        String reason,
        Double confidence,
        String formatted,
        Double lon,
        Double lat
) {}
