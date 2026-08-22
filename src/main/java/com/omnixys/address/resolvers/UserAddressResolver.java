package com.omnixys.address.resolvers;

import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.inputs.CreateUserAddressInput;
import com.omnixys.address.models.inputs.UpdateUserAddressInput;
import com.omnixys.address.models.inputs.UserAddressFilter;
import com.omnixys.address.models.payloads.UserAddressPayload;
import com.omnixys.address.services.UserAddressReadService;
import com.omnixys.address.services.UserAddressWriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserAddressResolver {

    private final UserAddressReadService readService;
    private final UserAddressWriteService writeService;

    @MutationMapping
    public UserAddress createUserAddress(@Argument @Valid CreateUserAddressInput input) {
        return writeService.createUserAddress(input);
    }

    @MutationMapping
    public UserAddress updateUserAddress(@Argument @Valid UpdateUserAddressInput input) {
        return writeService.updateUserAddress(input);
    }

    @MutationMapping
    public Boolean deleteUserAddressByUserId(@Argument UUID userId) {
        return writeService.deleteUserAddressByUserId(userId);
    }

    @QueryMapping
    public UserAddressPayload userAddressById(@Argument UUID id) {
        return readService.findById(id).orElse(null);
    }

    @QueryMapping
    public List<UserAddressPayload> getUserAddressesByUserId(@Argument UUID userId) {
        log.debug("getUserAddressesByUserId: userId={}", userId);
        return readService.findByUserId(userId);
    }

    @QueryMapping
    public List<UserAddressPayload> userAddresses(@Argument UserAddressFilter filter) {
        return readService.find(filter);
    }
}
