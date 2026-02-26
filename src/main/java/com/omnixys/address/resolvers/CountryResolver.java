package com.omnixys.address.resolvers;

import com.omnixys.address.models.entity.Country;
import com.omnixys.address.models.inputs.CountryFilterInput;
import com.omnixys.address.services.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CountryResolver {
    private final CountryService countryService;

    // =====================================================
    // SINGLE FETCH
    // =====================================================
    public Country countryById(@Argument UUID id) {
        log.debug("GraphQL: countryById id={}", id);
        return countryService.findById(id);
//        return countryMapper.toCountryPayload(country);
    }

    public Country countryByIso2(@Argument String iso2) {
        log.debug("GraphQL: countryByIso2 iso2={}", iso2);
        return countryService.findByIso2(iso2);
//        return countryMapper.toCountryPayload(country);
    }

    public Country countryByIso3(@Argument String iso3) {
        log.debug("GraphQL: countryByIso3 iso3={}", iso3);
        return countryService.findByIso3(iso3);
//        return countryMapper.toCountryPayload(country);
    }

    @QueryMapping
    public List<Country> getAllCountries() {
        return countryService.findAllCountries();
//        return countryMapper.toCountryPayloadList(countries);

    }

    // =====================================================
    // LIST WITH FILTER + PAGINATION
    // =====================================================
    public Iterable<Country> countries(
            @Argument CountryFilterInput filter,
            @Argument Integer page,
            @Argument Integer size
    ) {

        int pageNumber = page == null ? 0 : page;
        int pageSize = size == null ? 250 : size;

        log.info("GraphQL: countries page={} size={} filter={}",
                pageNumber, pageSize, filter);

        var pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by("name").ascending()
        );

        return countryService
                .findAll(filter, pageable)
                .stream()
                .toList();

//        return countryMapper.toCountryPayloadList(countries);
    }
}
