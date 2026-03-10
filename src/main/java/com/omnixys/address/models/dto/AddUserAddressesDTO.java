package com.omnixys.address.models.dto;

import java.util.UUID;

public record AddUserAddressesDTO(
        UUID userId,
        String token
) {
}
