package com.omnixys.address.models.inputs;

import com.omnixys.address.models.enums.AddressType;

import java.util.UUID;

public record UserAddressFilter(
        UUID userId,
        UUID countryId,
        UUID cityId,
        UUID postalCodeId,
        AddressType addressType
) {
}
