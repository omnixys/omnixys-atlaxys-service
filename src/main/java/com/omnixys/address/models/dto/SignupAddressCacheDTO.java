package com.omnixys.address.models.dto;

import com.omnixys.address.models.enums.AddressType;

import java.time.Instant;
import java.util.List;

public record SignupAddressCacheDTO(
        List<AddressItem> addresses,
        Meta meta
) {

    public record AddressItem(
            String street,
            String houseNumber,
            String postalCodeId,
            String cityId,
            String stateId,
            String countryId,
            AddressType addressType,
            String additionalInfo
    ) {}

    public record Meta(
            Instant createdAt,
            String ip,
            String userAgent
    ) {}
}