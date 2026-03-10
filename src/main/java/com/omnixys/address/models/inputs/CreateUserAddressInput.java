package com.omnixys.address.models.inputs;

import com.omnixys.address.models.enums.AddressType;

import java.util.UUID;

public record CreateUserAddressInput(
        UUID userId,
        AddressType addressType,
        UUID streetId,
        UUID houseNumberId,
        UUID postalCodeId,
        UUID cityId,
        UUID stateId,
        UUID countryId,
        String additionalInfo
) {}