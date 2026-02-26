package com.omnixys.address.models.inputs;

import lombok.Builder;

@Builder
public record CountryFilterInput(
        String name,
        String continent,
        String subregion,
        String currencyCode,
        String callingCode
) {}