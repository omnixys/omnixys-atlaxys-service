package com.omnixys.atlaxys.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CountryDetailsDTO(

        Integer id,
        String name,
        String iso3,
        String iso2,

        @JsonProperty("numeric_code")
        String numericCode,

        String phonecode,
        String capital,

        String currency,

        @JsonProperty("currency_name")
        String currencyName,

        @JsonProperty("currency_symbol")
        String currencySymbol,

        String tld,

        @JsonProperty("native")
        String nativeName,

        Long population,
        Long gdp,

        String region,

        @JsonProperty("region_id")
        Integer regionId,

        String subregion,

        @JsonProperty("subregion_id")
        Integer subregionId,

        String nationality,

        @JsonProperty("area_sq_km")
        Double areaSqKm,

        @JsonProperty("postal_code_format")
        String postalCodeFormat,

        @JsonProperty("postal_code_regex")
        String postalCodeRegex,

        List<TimezoneDTO> timezones,

        Map<String, String> translations,

        String latitude,
        String longitude,

        String wikiDataId

) {

    public record TimezoneDTO(
            String zoneName,
            Integer gmtOffset,
            String gmtOffsetName,
            String abbreviation,
            String tzName
    ) {}
}