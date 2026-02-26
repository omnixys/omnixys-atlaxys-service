package com.omnixys.address.services;

import com.omnixys.address.models.dto.GeoapifyGeocodeResponse;
import com.omnixys.address.models.inputs.AddressValidationInput;
import com.omnixys.address.models.payload.AddressValidationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeoapifyAddressValidationService {

    private final RestClient restClient = RestClient.create();

    @Value("${app.geoapify.apiKey}")
    private String apiKey;

    /**
     * Validates a user entered address against Geoapify Search Geocoding.
     * Rule (strict):
     * - valid only if exactly 1 result AND rank.confidence == 1
     */
    public AddressValidationPayload validateStrict(AddressValidationInput in) {

        validateInput(in);

        log.debug("Geoapify validateStrict called: street='{}', house='{}', zip='{}', city='{}', state='{}', country='{}'",
                in.street(), in.houseNumber(), in.postalCode(), in.city(), in.state(), in.country());

        GeoapifyGeocodeResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.geoapify.com")
                        .path("/v1/geocode/search")
                        .queryParam("housenumber", in.houseNumber())
                        .queryParam("street", in.street())
                        .queryParam("postcode", in.postalCode())
                        .queryParam("city", in.city())
                        .queryParam("state", in.state())
                        .queryParam("country", in.country())
                        .queryParam("format", "json")
                        .queryParam("apiKey", apiKey)
                        .build())
                .retrieve()
                .body(GeoapifyGeocodeResponse.class);

        List<GeoapifyGeocodeResponse.Result> results =
                response != null && response.results() != null ? response.results() : List.of();

        log.debug("Geoapify returned {} result(s).", results.size());

        if (results.isEmpty()) {
            log.warn("Address invalid: no results.");
            return new AddressValidationPayload(false, "NO_RESULTS", null, null, null, null);
        }

        if (results.size() != 1) {
            // strict rule: multiple results => invalid
            Double bestConfidence = results.stream()
                    .map(r -> r.rank() != null ? r.rank().confidence() : null)
                    .filter(Objects::nonNull)
                    .max(Double::compareTo)
                    .orElse(null);

            log.warn("Address invalid: multiple results (count={}), bestConfidence={}", results.size(), bestConfidence);
            return new AddressValidationPayload(false, "MULTIPLE_RESULTS", bestConfidence, null, null, null);
        }

        var r = results.getFirst();
        Double confidence = r.rank() != null ? r.rank().confidence() : null;

        log.debug("Single result: formatted='{}', resultType='{}', confidence={}",
                r.formatted(), r.resultType(), confidence);

        if (!isConfidenceOne(confidence)) {
            log.warn("Address invalid: confidence != 1 (confidence={}).", confidence);
            return new AddressValidationPayload(false, "CONFIDENCE_NOT_1", confidence, r.formatted(), r.lon(), r.lat());
        }

        if (!matchesExactly(in, r)) {
            return new AddressValidationPayload(
                    false,
                    "FIELDS_DO_NOT_MATCH",
                    confidence,
                    r.formatted(),
                    r.lon(),
                    r.lat()
            );
        }

        log.info("Address valid: formatted='{}'", r.formatted());
        return new AddressValidationPayload(true, "OK", confidence, r.formatted(), r.lon(), r.lat());
    }

    private static boolean isConfidenceOne(Double confidence) {
        // Protect against floating point weirdness
        return confidence != null && Math.abs(confidence - 1.0d) < 0.0000001d;
    }

    private static void validateInput(AddressValidationInput in) {
        // English comments for VS as requested
        if (in == null) throw new IllegalArgumentException("Input must not be null");

        if (!StringUtils.hasText(in.street())) throw new IllegalArgumentException("street is required");
        if (!StringUtils.hasText(in.houseNumber())) throw new IllegalArgumentException("houseNumber is required");
        if (!StringUtils.hasText(in.postalCode())) throw new IllegalArgumentException("postalCode is required");
        if (!StringUtils.hasText(in.city())) throw new IllegalArgumentException("city is required");
        if (!StringUtils.hasText(in.state())) throw new IllegalArgumentException("state is required");
        if (!StringUtils.hasText(in.country())) throw new IllegalArgumentException("country is required");
    }

    private boolean matchesExactly(AddressValidationInput in, GeoapifyGeocodeResponse.Result r) {

        if (r == null) return false;

        return equalsIgnoreCase(in.postalCode(), r.postcode())
                && equalsIgnoreCase(in.city(), r.city())
                && equalsIgnoreCase(in.state(), r.state())
                && equalsIgnoreCase(in.country(), r.country())
                && equalsIgnoreCase(in.houseNumber(), r.housenumber())
                && equalsIgnoreCase(in.street(), r.street());
    }

    private boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        return a.trim().equalsIgnoreCase(b.trim());
    }
}