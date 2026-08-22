package com.omnixys.address.models.mappers;

import com.omnixys.address.models.payloads.EventAddressPayload;
import com.omnixys.address.repository.EventAddressProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventAddressMapper {

    public EventAddressPayload toPayload(EventAddressProjection projection) {
        return new EventAddressPayload(
                projection.getId(),
                projection.getEventId(),
                projection.getCountry(),
                projection.getState(),
                projection.getCity(),
                projection.getPostalCode(),
                projection.getStreet(),
                projection.getHouseNumber(),
                projection.getAdditionalInfo(),
                projection.getLat(),
                projection.getLon()
        );
    }
}
