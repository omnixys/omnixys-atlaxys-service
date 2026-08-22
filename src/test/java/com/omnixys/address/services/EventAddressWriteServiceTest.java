package com.omnixys.address.services;

import com.omnixys.address.analytics.AnalyticsOutboxService;
import com.omnixys.address.repository.EventAddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventAddressWriteServiceTest {

    @Mock
    private EventAddressRepository repository;
    @Mock
    private CountryService countryService;
    @Mock
    private StateService stateService;
    @Mock
    private CityService cityService;
    @Mock
    private PostalCodeService postalCodeService;
    @Mock
    private StreetService streetService;
    @Mock
    private HouseNumberService houseNumberService;
    @Mock
    private AnalyticsOutboxService analyticsOutbox;
    @Mock
    private EventAddressReadService readService;

    @InjectMocks
    private EventAddressWriteService service;

    @Test
    void deletesDistinctRootAndChildEventAddresses() {
        var rootId = UUID.randomUUID();
        var childId = UUID.randomUUID();

        service.deleteEventAddressesByEventIds(List.of(rootId, childId, rootId));

        verify(repository).deleteByEventId(rootId);
        verify(repository).deleteByEventId(childId);
        verify(analyticsOutbox).enqueue(eq("address.deleted.v1"), eq("AddressDeleted"),
                eq("event-address"), eq(rootId), isNull(), anyMap());
        verify(analyticsOutbox).enqueue(eq("address.deleted.v1"), eq("AddressDeleted"),
                eq("event-address"), eq(childId), isNull(), anyMap());
    }

    @Test
    void rejectsDeleteWithoutValidEventIds() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteEventAddressesByEventIds(List.of()));
        verify(repository, never()).deleteByEventId(any());
        verify(analyticsOutbox, never()).enqueue(anyString(), anyString(), anyString(),
                any(), anyString(), anyMap());
    }
}
