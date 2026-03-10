package com.omnixys.address.models.dto;

import com.omnixys.address.models.enums.AddressType;

import java.util.UUID;

public record CreateUserAddressDTO(
        UUID countryId,
        UUID stateId,
        UUID cityId,
        UUID postalCodeId,
        String street,
        String houseNumber,
        String additionalInfo,
        AddressType addressType
) {
}
