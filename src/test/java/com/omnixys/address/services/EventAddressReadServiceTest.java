package com.omnixys.address.services;

import com.omnixys.address.errors.AddressNotFoundException;
import com.omnixys.address.models.mappers.EventAddressMapper;
import com.omnixys.address.models.payloads.EventAddressPayload;
import com.omnixys.address.repository.EventAddressProjection;
import com.omnixys.address.repository.EventAddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventAddressReadServiceTest {

    @Mock
    private EventAddressRepository repository;

    @Mock
    private EventAddressMapper mapper;

    @InjectMocks
    private EventAddressReadService service;

    @Test
    void findByEventIdMapsTypedProjection() {
        var id = UUID.randomUUID();
        var eventId = UUID.randomUUID();

        var projection = org.mockito.Mockito.mock(EventAddressProjection.class);
        when(repository.findProjectedByEventId(eventId)).thenReturn(Optional.of(projection));
        when(mapper.toPayload(projection)).thenReturn(
                new EventAddressPayload(id, eventId, "Germany", null, "Stuttgart",
                        null, null, null, null, 48.7758, 9.1829));

        var result = service.findByEventId(eventId);

        assertEquals(id, result.id());
        assertEquals(eventId, result.eventId());
        assertEquals("Germany", result.country());
        assertEquals("Stuttgart", result.city());
        assertEquals(48.7758, result.lat());
        assertEquals(9.1829, result.lon());
    }

    @Test
    void findByIdMapsTypedProjection() {
        var id = UUID.randomUUID();
        var eventId = UUID.randomUUID();

        var projection = org.mockito.Mockito.mock(EventAddressProjection.class);
        when(repository.findProjectedById(id)).thenReturn(Optional.of(projection));
        when(mapper.toPayload(projection)).thenReturn(
                new EventAddressPayload(id, eventId, "Germany", null, "Stuttgart",
                        null, null, null, null, 48.7758, 9.1829));

        var result = service.findById(id);

        assertEquals(id, result.id());
        assertEquals(eventId, result.eventId());
    }

    @Test
    void findByEventIdReportsMissingAddress() {
        var eventId = UUID.randomUUID();
        when(repository.findProjectedByEventId(eventId)).thenReturn(Optional.empty());

        assertThrows(AddressNotFoundException.class, () -> service.findByEventId(eventId));
    }
}
