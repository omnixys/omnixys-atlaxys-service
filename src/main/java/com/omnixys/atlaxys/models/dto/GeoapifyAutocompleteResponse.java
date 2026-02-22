package com.omnixys.atlaxys.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoapifyAutocompleteResponse(
        List<Result> results
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            String formatted,
            String country,
            @JsonProperty("country_code") String countryCode,
            String state,
            String city,
            String postcode,
            String street,
            String housenumber,
            Double lon,
            Double lat,
            Rank rank
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rank(
            Double confidence,
            @JsonProperty("confidence_street_level") Double confidenceStreetLevel,
            @JsonProperty("confidence_building_level") Double confidenceBuildingLevel
    ) {}
}