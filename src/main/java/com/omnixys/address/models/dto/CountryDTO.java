package com.omnixys.address.models.dto;

import java.util.List;

public record CountryDTO(
        String name,
        String alpha2Code,
        String alpha3Code,
        String region,
        String subregion,
        String nativeName,
        List<String> callingCodes,
        List<String> timezones,
        List<CurrencyDTO> currencies,
        List<LanguageDTO> languages,
        Flags flags
) {

    public record Flags(String svg, String png) {}

    public record CurrencyDTO(
            String code,
            String name,
            String symbol
    ) {}

    public record LanguageDTO(
            String iso639_1,
            String iso639_2,
            String name,
            String nativeName
    ) {}
}