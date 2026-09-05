package com.omnixys.address.resolvers;

import com.omnixys.address.models.entitys.UserAddress;
import com.omnixys.address.models.enums.AddressType;
import com.omnixys.address.models.inputs.CreateUserAddressInput;
import com.omnixys.address.services.UserAddressReadService;
import com.omnixys.address.services.UserAddressWriteService;
import com.omnixys.context.ContextAccessor;
import com.omnixys.context.ContextSnapshot;
import com.omnixys.context.PrincipalContext;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAddressResolverTest {

    @Mock
    private UserAddressReadService readService;

    @Mock
    private UserAddressWriteService writeService;

    @InjectMocks
    private UserAddressResolver resolver;

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private static final UUID PRINCIPAL_USER_ID = UUID.fromString("01b05f6a-5800-7000-8000-000000000001");

    private void setupPrincipal() {
        var principal = new PrincipalContext(
                "test-sub", PRINCIPAL_USER_ID.toString(), PRINCIPAL_USER_ID.toString(),
                "test-tenant", java.util.List.of(), null, null, null
        );
        var client = new com.omnixys.context.ClientMetadata(
                "127.0.0.1", null, null, null, null, null, null, null, null
        );
        var transport = new com.omnixys.context.TransportMetadata(
                "GRAPHQL", "POST", null, null, null, null, null, null, null, null, null, null, null
        );
        var trace = new com.omnixys.context.TraceMetadata("test-trace", "test-span");
        var snapshot = new ContextSnapshot(
                "test-request", "test-correlation", System.currentTimeMillis(),
                null, principal, client, transport, trace
        );
        ContextAccessor.set(snapshot);
    }

    @AfterEach
    void clearContext() {
        ContextAccessor.clear();
    }

    @Test
    void createUserAddressWithValidInput() {
        setupPrincipal();

        var streetId = UUID.randomUUID();
        var postalCodeId = UUID.randomUUID();
        var cityId = UUID.randomUUID();
        var stateId = UUID.randomUUID();
        var countryId = UUID.randomUUID();
        var addressId = UUID.randomUUID();

        var input = new CreateUserAddressInput(
                UUID.randomUUID(),
                AddressType.HOME,
                streetId,
                postalCodeId,
                cityId,
                stateId,
                countryId,
                null,
                null
        );

        var address = UserAddress.builder()
                .id(addressId)
                .userId(PRINCIPAL_USER_ID)
                .addressType(AddressType.HOME)
                .build();

        when(writeService.createUserAddress(any(CreateUserAddressInput.class), eq(PRINCIPAL_USER_ID)))
                .thenReturn(address);

        var result = resolver.createUserAddress(input);

        assertNotNull(result);
        assertEquals(addressId, result.getId());
        assertEquals(PRINCIPAL_USER_ID, result.getUserId());
        assertEquals(AddressType.HOME, result.getAddressType());
    }

    @Test
    void createUserAddressWithNullRequiredFieldsFailsValidation() {
        var input = new CreateUserAddressInput(
                UUID.randomUUID(),
                AddressType.HOME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        var violations = validator.validate(input);
        assertFalse(violations.isEmpty());
        var propertyPaths = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .toList();
        assertTrue(propertyPaths.containsAll(
                java.util.List.of("streetId", "postalCodeId", "cityId", "stateId", "countryId")));
    }
}
