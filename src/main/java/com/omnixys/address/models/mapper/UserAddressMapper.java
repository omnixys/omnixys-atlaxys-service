package com.omnixys.address.models.mapper;

import com.omnixys.address.models.entity.UserAddress;
import com.omnixys.address.models.inputs.CreateUserAddressInput;
import com.omnixys.address.models.payload.UserAddressPayload;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserAddressMapper {
    UserAddress toUserAddress(CreateUserAddressInput createUserAddressInput);
    UserAddressPayload toPayload(UserAddress userAddress);
}
