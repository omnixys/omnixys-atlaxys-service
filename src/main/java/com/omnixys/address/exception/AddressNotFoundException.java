package com.omnixys.address.exception;

import java.util.UUID;

public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(UUID id) {
        super("UserAddress not found with id: " + id);
    }
}
