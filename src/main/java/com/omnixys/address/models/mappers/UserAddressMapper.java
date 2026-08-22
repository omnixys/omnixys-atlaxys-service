package com.omnixys.address.models.mappers;

import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.payloads.UserAddressPayload;
import com.omnixys.address.services.CityService;
import com.omnixys.address.services.CountryService;
import com.omnixys.address.services.HouseNumberService;
import com.omnixys.address.services.PostalCodeService;
import com.omnixys.address.services.StateService;
import com.omnixys.address.services.StreetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserAddressMapper {

    private final CountryService countryService;
    private final StateService stateService;
    private final CityService cityService;
    private final PostalCodeService postalCodeService;
    private final StreetService streetService;
    private final HouseNumberService houseNumberService;

    public UserAddressPayload toPayload(UserAddress address) {

        log.debug("Resolving address values for id={}", address.getId());

        String country = countryService.findById(address.getCountryId()).getName();

        String state = address.getStateId() != null
                ? stateService.findById(address.getStateId()).getName()
                : null;

        String city = cityService.findById(address.getCityId()).getName();

        String postalCode = address.getPostalCodeId() != null
                ? postalCodeService.findById(address.getPostalCodeId()).getCode()
                : null;

        String street = streetService.findById(address.getStreetId()).getName();

        String houseNumber = houseNumberService
                .findById(address.getHouseNumberId())
                .getNumber();

        return new UserAddressPayload(
                address.getId(),
                address.getUserId(),
                country,
                state,
                city,
                postalCode,
                street,
                houseNumber,
                address.getAdditionalInfo(),
                address.getAddressType()
        );
    }
}
