package com.omnixys.address.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoapifyGeocodeResponse(
        List<Result> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Result(
            @JsonProperty("result_type") String resultType,
            String country,
            @JsonProperty("country_code") String countryCode,
            String state,
            String city,
            String postcode,
            String street,
            String housenumber,
            String formatted,
            Double lon,
            Double lat,
            Rank rank
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Rank(
            Double confidence,
            @JsonProperty("confidence_city_level") Double confidenceCityLevel,
            @JsonProperty("confidence_street_level") Double confidenceStreetLevel,
            @JsonProperty("confidence_building_level") Double confidenceBuildingLevel,
            @JsonProperty("match_type") String matchType
    ) {}
}
