package com.omnixys.atlaxys.resolvers;

import com.omnixys.atlaxys.models.inputs.CityFilterInput;
import com.omnixys.atlaxys.models.entity.City;
import com.omnixys.atlaxys.services.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CityResolver {
    private final CityService cityService;
    // -----------------------------------
    // findById
    // -----------------------------------
    public City cityById(@Argument UUID id) {
        return cityService.findById(id);
    }

    // -----------------------------------
    // find (filtered + pageable)
    // -----------------------------------
    public List<City> cities(
            @Argument CityFilterInput filter,
            @Argument Integer page,
            @Argument Integer size
    ) {

        var pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20
        );

        return cityService.find(
                filter != null ? filter.stateId() : null,
                filter != null ? filter.name() : null,
                filter != null ? filter.type() : null,
                filter != null ? filter.minPopulation() : null,
                filter != null ? filter.maxPopulation() : null,
                pageable
        ).getContent();
    }

    // -----------------------------------
    // findByState
    // -----------------------------------
    @QueryMapping
    public List<City> getCitiesByState(@Argument UUID stateId) {
        return cityService.findByState(stateId);
    }

    @QueryMapping
    public City getCitiesByPostalCode(@Argument UUID postalCodeId) {
        return cityService.findByPostalCode(postalCodeId);
    }
}
