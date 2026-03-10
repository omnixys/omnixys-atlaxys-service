package com.omnixys.address.kafka;

public record TraceDTO(
        String traceId,
        String spanId
) {}