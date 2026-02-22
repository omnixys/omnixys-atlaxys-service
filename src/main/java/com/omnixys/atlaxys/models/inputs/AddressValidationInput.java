package com.omnixys.atlaxys.models.inputs;

public record AddressValidationInput(
        String street,
        String houseNumber,
        String postalCode,
        String city,
        String state,
        String country
) {}