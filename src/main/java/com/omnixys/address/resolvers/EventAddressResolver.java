package com.omnixys.address.resolvers;

import com.omnixys.address.models.inputs.CreateEventAddressDTO;
import com.omnixys.address.models.inputs.CreateEventAddressInput;
import com.omnixys.address.models.inputs.UpdateEventAddressInput;
import com.omnixys.address.models.payloads.EventAddressPayload;
import com.omnixys.address.services.EventAddressReadService;
import com.omnixys.address.services.EventAddressWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventAddressResolver {

    private final EventAddressReadService readService;
    private final EventAddressWriteService writeService;

    @MutationMapping
    public EventAddressPayload createEventAddress(@Argument @Valid CreateEventAddressInput input) {
        return writeService.createEventAddress(input);
    }

    @MutationMapping
    public EventAddressPayload updateEventAddress(@Argument @Valid UpdateEventAddressInput input) {
        return writeService.updateEventAddress(input);
    }

    @MutationMapping
    public Boolean deleteEventAddressByEventId(@Argument UUID eventId) {
        return writeService.deleteEventAddressByEventId(eventId);
    }

    @QueryMapping
    public EventAddressPayload eventAddressById(@Argument UUID id) {
        return readService.findById(id);
    }

    @QueryMapping
    public EventAddressPayload getEventAddressByEventId(@Argument UUID eventId) {
        log.debug("getEventAddressesByEventId: eventId={}", eventId);
        return readService.findByEventId(eventId);
    }
}
