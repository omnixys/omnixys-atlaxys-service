package com.omnixys.atlaxys.resolvers;

import com.omnixys.atlaxys.models.inputs.PostalCodeFilterInput;
import com.omnixys.atlaxys.models.entity.PostalCode;
import com.omnixys.atlaxys.services.PostalCodeService;
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
public class PostalCodeQueryResolver {

    private final PostalCodeService postalCodeService;

    // -----------------------------------
    // findById
    // -----------------------------------
    public PostalCode postalCodeById(@Argument UUID id) {
        return postalCodeService.findById(id);
    }

    // -----------------------------------
    // find (filtered + pageable)
    // -----------------------------------

    public List<PostalCode> postalCodes(
            @Argument PostalCodeFilterInput filter,
            @Argument Integer page,
            @Argument Integer size
    ) {

        var pageable = PageRequest.of(
                page != null ? page : 0,
                size != null ? size : 20
        );

        return postalCodeService.find(
                filter != null ? filter.countryId() : null,
                filter != null ? filter.cityId() : null,
                filter != null ? filter.zip() : null,
                pageable
        ).getContent();
    }

    // -----------------------------------
    // findByState
    // -----------------------------------
    @QueryMapping
    public List<PostalCode> getPostalCodesByState(@Argument UUID stateId) {
        return postalCodeService.findByState(stateId);
    }

    // -----------------------------------
    // postalCodesByCity
    // -----------------------------------
    @QueryMapping
    public List<PostalCode> getPostalCodesByCity(@Argument UUID cityId) {
        return postalCodeService.findByCityId(cityId);
    }
}