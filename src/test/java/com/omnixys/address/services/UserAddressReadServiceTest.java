package com.omnixys.address.services;

import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.enums.AddressType;
import com.omnixys.address.models.mappers.UserAddressMapper;
import com.omnixys.address.models.payloads.UserAddressPayload;
import com.omnixys.address.repository.UserAddressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAddressReadServiceTest {

    @Mock
    private UserAddressRepository repository;

    @Mock
    private UserAddressMapper mapper;

    @InjectMocks
    private UserAddressReadService service;

    @Test
    void findByUserIdMapsEntitiesToPayloads() {
        var userId = UUID.randomUUID();
        var address = UserAddress.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .addressType(AddressType.HOME)
                .build();
        var payload = new UserAddressPayload(
                address.getId(), userId, "Germany", "BW", "Stuttgart",
                "70173", "Main St", "1", null, AddressType.HOME);

        when(repository.findByUserId(userId)).thenReturn(List.of(address));
        when(mapper.toPayload(address)).thenReturn(payload);

        var result = service.findByUserId(userId);

        assertEquals(1, result.size());
        assertEquals(payload, result.getFirst());
    }

    @Test
    void findByIdMapsEntityToPayload() {
        var id = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var address = UserAddress.builder()
                .id(id)
                .userId(userId)
                .addressType(AddressType.WORK)
                .build();
        var payload = new UserAddressPayload(
                id, userId, "Germany", null, "Berlin",
                null, null, null, "floor 3", AddressType.WORK);

        when(repository.findById(id)).thenReturn(Optional.of(address));
        when(mapper.toPayload(address)).thenReturn(payload);

        var result = service.findById(id);

        assertTrue(result.isPresent());
        assertEquals(payload, result.get());
    }
}
