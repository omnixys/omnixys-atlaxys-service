package com.omnixys.atlaxys.resolvers;

import com.omnixys.atlaxys.models.entity.State;
import com.omnixys.atlaxys.models.inputs.StateFilterInput;
import com.omnixys.atlaxys.models.payload.StatePage;
import com.omnixys.atlaxys.services.StateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StateResolver {

    private final StateService stateService;

    // =====================================================
    // FIND BY ID
    // =====================================================
    public State stateById(@Argument UUID id) {

        log.debug("GraphQL: stateById id={}", id);

        return stateService.findById(id);
    }

    // =====================================================
    // FIND BY CODE (ISO3166-2)
    // =====================================================
    public State stateByCode(@Argument String code) {

        log.debug("GraphQL: stateByCode code={}", code);

        return stateService.findByCode(code);
    }

    // =====================================================
    // FIND ALL (WITH FILTER)
    // =====================================================
    public StatePage states(
            @Argument StateFilterInput filter,
            @Argument int page,
            @Argument int size
    ) {

        log.debug("GraphQL: states filter={} page={} size={}",
                filter, page, size);

        var result =
                stateService.findAll(filter, PageRequest.of(page, size));

        return new StatePage(result);
    }

    // =====================================================
    // BY COUNTRY ID
    // =====================================================
    public StatePage statesByCountryId(
            @Argument UUID countryId,
            @Argument int page,
            @Argument int size
    ) {

        log.debug("GraphQL: statesByCountryId countryId={}", countryId);

        var result =
                stateService.findByCountryId(
                        countryId,
                        PageRequest.of(page, size)
                );

        return new StatePage(result);
    }

    // =====================================================
    // BY COUNTRY ISO2
    // =====================================================
    public StatePage statesByCountryIso2(
            @Argument String iso2,
            @Argument int page,
            @Argument int size
    ) {

        log.debug("GraphQL: statesByCountryIso2 iso2={}", iso2);

        var result =
                stateService.findByCountryIso2(
                        iso2,
                        PageRequest.of(page, size)
                );

        return new StatePage(result);
    }

    // =====================================================
    // BY COUNTRY ISO3
    // =====================================================
    public StatePage statesByCountryIso3(
            @Argument String iso3,
            @Argument int page,
            @Argument int size
    ) {

        log.debug("GraphQL: statesByCountryIso3 iso3={}", iso3);

        var result =
                stateService.findByCountryIso3(
                        iso3,
                        PageRequest.of(page, size)
                );

        return new StatePage(result);
    }

    @QueryMapping
    public List<State> getStatesByCountry(
            @Argument UUID countryId
    ) {

        log.debug("GraphQL: getStatesByCountry countryId={}", countryId);
        return stateService.findByCountryId(countryId);

    }
}