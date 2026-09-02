package com.omnixys.address.resolvers;

import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.inputs.CreateUserAddressInput;
import com.omnixys.address.models.inputs.UpdateUserAddressInput;
import com.omnixys.address.models.inputs.UserAddressFilter;
import com.omnixys.address.models.payloads.UserAddressPayload;
import com.omnixys.address.services.UserAddressReadService;
import com.omnixys.address.services.UserAddressWriteService;
import com.omnixys.context.ContextAccessor;
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

    private UUID resolveCurrentUserId() {
        var context = ContextAccessor.get();
        if (context == null || context.principal() == null || context.principal().userId() == null) {
            throw new IllegalStateException("No authenticated principal with omnixys_user_id");
        }
        return UUID.fromString(context.principal().userId());
    }

    @MutationMapping
    public UserAddress createUserAddress(@Argument @Valid CreateUserAddressInput input) {
        UUID principalUserId = resolveCurrentUserId();
        return writeService.createUserAddress(input, principalUserId);
    }

    @MutationMapping
    public UserAddress updateUserAddress(@Argument @Valid UpdateUserAddressInput input) {
        UUID principalUserId = resolveCurrentUserId();
        return writeService.updateUserAddress(input, principalUserId);
    }

    @MutationMapping
    public Boolean deleteUserAddressByUserId() {
        UUID principalUserId = resolveCurrentUserId();
        return writeService.deleteUserAddressByUserId(principalUserId);
    }

    @QueryMapping
    public UserAddressPayload userAddressById(@Argument UUID id) {
        return readService.findById(id).orElse(null);
    }

    @QueryMapping
    public List<UserAddressPayload> getUserAddressesByUserId() {
        UUID principalUserId = resolveCurrentUserId();
        log.debug("getUserAddressesByUserId: userId={}", principalUserId);
        return readService.findByUserId(principalUserId);
    }

    @QueryMapping
    public List<UserAddressPayload> userAddresses(@Argument UserAddressFilter filter) {
        return readService.find(filter);
    }
}
