package com.omnixys.address.models.inputs;

import com.omnixys.address.models.enums.AddressType;

import java.util.UUID;

public record UpdateUserAddressInput(
        UUID id,
        AddressType addressType,
        String additionalInfo
) {
}
