package com.omnixys.atlaxys.models.dto;

public record CountryApiResponse(
        String name,
        String alpha2Code,
        String alpha3Code,
        Flags flags
) {
    public record Flags(
            String svg,
            String png
    ) {}
}

