package com.omnixys.address.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StateDTO(

        Long id,

        String name,

        Long country_id,
        String country_code,
        String country_name,

        String iso2,
        String iso3166_2,

        String fips_code,

        String type,
        Integer level,

        Long parent_id,

        String latitude,
        String longitude,

        String timezone,

        Long population
) {}