package com.omnixys.address.kafka;

public record EventEnvelope<T>(
        String event,
        String service,
        String version,
        TraceDTO trace,
        T payload
) {}