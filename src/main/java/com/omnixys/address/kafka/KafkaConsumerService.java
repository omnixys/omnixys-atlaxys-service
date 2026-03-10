package com.omnixys.address.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnixys.address.models.dto.AddUserAddressesDTO;
import com.omnixys.address.models.dto.DeleteAddressesDTO;
import com.omnixys.address.models.dto.TestEvent;
import com.omnixys.address.services.UserAddressService;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.omnixys.address.kafka.KafkaTopicProperties.TOPIC_CREATE_USER_ADDRESSES;
import static com.omnixys.address.kafka.KafkaTopicProperties.TOPIC_TEST;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.Tracer;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    private final ApplicationContext context;
    private final UserAddressService userAddressService;
    private final ObjectMapper objectMapper;

    public static final String GROUP_ID = "${app.groupId}";

    private final Tracer tracer;
//    private final LoggerPlusFactory factory;
//    private LoggerPlus logger() {
//        return factory.getLogger(getClass());
//    }

    @KafkaListener(topics = "test")
    public void test(byte[] payload) throws Exception {

        TestEvent event =
                objectMapper.readValue(payload, TestEvent.class);

        log.info("Received test event {}", event.message());
    }


    @KafkaListener(topics = "address.createUserAddresses.authentication")
    public void consumeUserAddress(byte[] payload) throws Exception {

        EventEnvelope<AddUserAddressesDTO> event =
                objectMapper.readValue(
                        payload,
                        new TypeReference<EventEnvelope<AddUserAddressesDTO>>() {}
                );

        AddUserAddressesDTO dto = event.payload();

        log.info("Received address event {}", dto);

        userAddressService.createUserAddresses(dto);
    }

    @KafkaListener(topics = "address.delete.authentication")
    public void deleteAddresses(byte[] payload) throws Exception {

        EventEnvelope<DeleteAddressesDTO> event =
                objectMapper.readValue(
                        payload,
                        new TypeReference<EventEnvelope<DeleteAddressesDTO>>() {}
                );

        var dto = event.payload();

        log.info("Received delete event {}", dto);

        userAddressService.deleteUserAddressByUserId(dto.id());
    }

//    @KafkaListener(topics = TOPIC_CREATE_USER_ADDRESSES, groupId = "${app.groupId}")
//    @Observed(name = "address.kafka.consume")
//    public void consumeUserAddress(ConsumerRecord<String, AddUserAddressesDTO> record) {
//        final var headers = record.headers();
//        final var userIdAndToken = record.value();
//
//        final var traceParent = getHeader(headers, "traceparent");
//
//        SpanContext linkedContext = null;
//        if (traceParent != null && traceParent.startsWith("00-")) {
//            String[] parts = traceParent.split("-");
//            if (parts.length == 4) {
//                String traceId = parts[1];
//                String spanId = parts[2];
//                boolean sampled = "01".equals(parts[3]);
//
//                linkedContext = SpanContext.createFromRemoteParent(
//                    traceId,
//                    spanId,
//                    sampled ? TraceFlags.getSampled() : TraceFlags.getDefault(),
//                    TraceState.getDefault()
//                );
//            }
//        }
//
//        // ✨ 2. Starte neuen Trace mit Link (nicht als Parent!)
//        SpanBuilder spanBuilder = tracer.spanBuilder("address.kafka.consume")
//            .setSpanKind(SpanKind.CONSUMER)
//            .setAttribute("messaging.system", "kafka")
//            .setAttribute("messaging.destination", TOPIC_CREATE_USER_ADDRESSES)
//            .setAttribute("messaging.operation", "consume");
//
//        if (linkedContext != null && linkedContext.isValid()) {
//            spanBuilder.addLink(linkedContext);
//        }
//
//        Span span = spanBuilder.startSpan();
//
//        try (Scope scope = span.makeCurrent()) {
//            assert scope != null;
//            log.info("📥 Empfangene Nachricht auf '{}': {}", TOPIC_CREATE_USER_ADDRESSES, userIdAndToken);
//            userAddressService.createUserAddresses(userIdAndToken);
//            span.setStatus(StatusCode.OK);
//        } catch (Exception e) {
//            span.recordException(e);
//            span.setStatus(StatusCode.ERROR, "Kafka-Fehler");
//            log.error("❌ Fehler beim Erstellen des Kontos", e);
//        } finally {
//            span.end();
//        }
//    }

    private String getHeader(Headers headers, String key) {
        Header header = headers.lastHeader(key);
        return header != null ? new String(header.value(), StandardCharsets.UTF_8) : null;
    }

//    @Observed(name = "kafka-consume.invoice.orchestration")
//    @KafkaListener(
//        topics = {
//            TOPIC_INVOICE_SHUTDOWN_ORCHESTRATOR,
//            TOPIC_INVOICE_START_ORCHESTRATOR,
//            TOPIC_INVOICE_RESTART_ORCHESTRATOR
//        },
//        groupId = "${app.groupId}"
//    )
//    public void handlePersonScoped(ConsumerRecord<String, String> record) {
//        final String topic = record.topic();
//        logger().info("Person-spezifisches Kommando empfangen: {}", topic);
//
//        switch (topic) {
//            case TOPIC_INVOICE_SHUTDOWN_ORCHESTRATOR -> shutdown();
//            case TOPIC_INVOICE_RESTART_ORCHESTRATOR -> restart();
//            case TOPIC_INVOICE_START_ORCHESTRATOR -> logger().info("Startsignal für Person-Service empfangen");
//        }
//    }

//    @Observed(name = "kafka-consume.all.orchestration")
//    @KafkaListener(
//        topics = {
//            TOPIC_ALL_SHUTDOWN_ORCHESTRATOR,
//            TOPIC_ALL_START_ORCHESTRATOR,
//            TOPIC_ALL_RESTART_ORCHESTRATOR
//        },
//        groupId = "${app.groupId}"
//    )
//    public void handleGlobalScoped(ConsumerRecord<String, String> record) {
//        final String topic = record.topic();
//        logger().info("Globales Systemkommando empfangen: {}", topic);
//
//        switch (topic) {
//            case TOPIC_ALL_SHUTDOWN_ORCHESTRATOR -> shutdown();
//            case TOPIC_ALL_RESTART_ORCHESTRATOR -> restart();
//            case TOPIC_ALL_START_ORCHESTRATOR -> logger().info("Globales Startsignal empfangen");
//        }
//    }

    private void shutdown() {
        try {
            log.info("→ Anwendung wird heruntergefahren (Shutdown-Kommando).");
            ((ConfigurableApplicationContext) context).close();
        } catch (Exception e) {
            log.error("Fehler beim Shutdown: {}", e.getMessage(), e);
        }
    }


    private void restart() {
        log.info("→ Anwendung wird neugestartet (Restart-Kommando).");
        ((ConfigurableApplicationContext) context).close();
        // Neustart durch externen Supervisor erwartet
    }
}
