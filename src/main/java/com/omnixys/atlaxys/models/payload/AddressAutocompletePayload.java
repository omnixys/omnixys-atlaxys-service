package com.omnixys.atlaxys.models.payload;

public record AddressAutocompletePayload(
        String formatted,
        String street,
        String houseNumber,
        String postalCode,
        String city,
        String state,
        String country,
        Double confidence,
        Double lat,
        Double lon
) {}