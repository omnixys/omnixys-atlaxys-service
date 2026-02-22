package com.omnixys.atlaxys.resolvers;

import com.omnixys.atlaxys.models.payload.AddressAutocompletePayload;
import com.omnixys.atlaxys.services.GeoapifyAutocompleteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AddressAutocompleteQueryResolver {

    private final GeoapifyAutocompleteService autocompleteService;

    @QueryMapping
    public List<AddressAutocompletePayload> addressAutocomplete(
            @Argument String text,
            @Argument String countryCode,
            @Argument Integer limit
    ) {

        log.debug("GraphQL addressAutocomplete called: text='{}'", text);

        return autocompleteService.autocomplete(text, countryCode, limit);
    }
}