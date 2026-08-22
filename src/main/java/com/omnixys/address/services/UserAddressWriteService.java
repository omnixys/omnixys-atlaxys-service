package com.omnixys.address.services;

import com.omnixys.address.errors.AddressNotFoundException;
import com.omnixys.address.models.dtos.AddUserAddressesDTO;
import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.inputs.CreateUserAddressInput;
import com.omnixys.address.models.inputs.UpdateUserAddressInput;
import com.omnixys.address.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserAddressWriteService {

    private final UserAddressRepository repository;
    private final StreetService streetService;
    private final HouseNumberService houseNumberService;
    private final AddressCacheService addressCacheService;

    public UserAddress createUserAddress(CreateUserAddressInput input) {

        log.debug("Creating user address for userId={}", input.userId());

        UserAddress address = new UserAddress();
        address.setUserId(input.userId());
        address.setCountryId(input.countryId());
        address.setStateId(input.stateId());
        address.setCityId(input.cityId());
        address.setPostalCodeId(input.postalCodeId());
        address.setStreetId(input.streetId());
        address.setHouseNumberId(input.houseNumberId());
        address.setAdditionalInfo(input.additionalInfo());
        address.setAddressType(input.addressType());

        return repository.save(address);
    }

    public void createUserAddresses(final AddUserAddressesDTO dto) {

        log.debug("Creating addresses from signup token");

        var tokenData = addressCacheService.getSignupAddressToken(dto.token());

        var userId = dto.userId();

        List<UserAddress> result = new ArrayList<>();

        for (var addressItem : tokenData.addresses()) {

            var streetId = streetService.findByNameAndCityId(addressItem.street(), UUID.fromString(addressItem.cityId())).getId();
            var houseNumberId = houseNumberService.findByHouseNumberAndStreetId(addressItem.houseNumber(), streetId).getId();

            UserAddress address = new UserAddress();
            address.setUserId(userId);
            address.setCountryId(UUID.fromString(addressItem.countryId()));
            address.setStateId(UUID.fromString(addressItem.stateId()));
            address.setCityId(UUID.fromString(addressItem.cityId()));
            address.setPostalCodeId(UUID.fromString(addressItem.postalCodeId()));
            address.setStreetId(streetId);
            address.setHouseNumberId(houseNumberId);
            address.setAdditionalInfo(addressItem.additionalInfo());
            address.setAddressType(addressItem.addressType());

            result.add(repository.save(address));
        }

        log.debug("Added UserAddresses for UserId {}= {}", userId, result);

        addressCacheService.deleteToken(dto.token());
    }

    public UserAddress updateUserAddress(UpdateUserAddressInput input) {

        log.debug("Updating user address id={}", input.id());

        UserAddress address = repository.findById(input.id())
                .orElseThrow(() -> new AddressNotFoundException(input.id()));

        if (input.addressType() != null) {
            address.setAddressType(input.addressType());
        }

        if (input.additionalInfo() != null) {
            address.setAdditionalInfo(input.additionalInfo());
        }

        return repository.save(address);
    }

    public boolean deleteUserAddressByUserId(UUID userId) {

        log.debug("Deleting user addresses for userId={}", userId);

        repository.deleteByUserId(userId);

        return true;
    }
}
