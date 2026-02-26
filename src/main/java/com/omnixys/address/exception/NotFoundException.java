package com.omnixys.address.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RuntimeException, falls kein Customer gefunden wurde.
 */
@Getter
public final class NotFoundException extends RuntimeException {
    /**
     * Nicht-vorhandene ID.
     */
    private final UUID id;

    private final String error;

    /**
     * Suchkriterien, zu denen nichts gefunden wurde.
     */
    private final Map<String, List<Object>> suchkriterien;

    public NotFoundException(final UUID id) {
        super(String.format("Kein Bankkonto mit der ID %s gefunden.", id));
        this.id = id;
        suchkriterien = null;
        error = null;
    }

    public NotFoundException(final Map<String, List<Object>> searchCriteria) {
        super("Keine Kunden gefunden mit diesen Suchkriterien gefunden.");
        id = null;
        this.suchkriterien = searchCriteria;
        error = null;
    }

    public NotFoundException() {
        super("Keine Kunden gefunden.");
        id = null;
        suchkriterien = null;
        error = null;
    }

    public NotFoundException(final String error) {
        super(error);
        id = null;
        suchkriterien = null;
        this.error = error;
    }
}
