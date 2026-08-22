package com.omnixys.address.models.dtos;

import java.util.UUID;

public record AddUserAddressesDTO(
        UUID userId,
        String token
) {
}
