package com.omnixys.address.kafka;

import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service zum Versenden von Kafka-Nachrichten im Zusammenhang mit Personenereignissen.
 * <p>
 * Unterstützte Ereignisse:
 * <ul>
 *     <li>Kundenmail nach Erstellung</li>
 *     <li>Erstellen eines Kontos</li>
 *     <li>Erstellen und Löschen eines Warenkorbs</li>
 * </ul>
 * </p>
 *
 * <p>
 * Die Topics folgen dem Schema: <code>service.entität.ereignis</code>
 * z.B. <code>shopping-cart.customer.created</code>
 * </p>
 *
 * @author <a href="mailto:caleb-script@outlook.de">Caleb Gyamfi</a>
 * @since 01.05.2025
 * @version 2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisherService {

//    private final KafkaTemplate<String, Object> kafkaTemplate;
//    private final Tracer tracer;
//    private final KafkaUtilService kafkaUtilService;
////    private final AppProperties appProperties;


    /**
     * Versendet ein Logging-Event an das zentrale Logging-System via Kafka.
     *
     * @param level   z.B. INFO, WARN, DEBUG, ERROR
     * @param message Die zu loggende Nachricht
     * @param context Kontext wie Klassen- oder Methodenname
     */
//    @Observed(name = "kafka-publisher.log")
//    public void log(String level, String message, String serviceName, String context) {
//        SpanContext spanContext = Span.current().getSpanContext();
//
//        final var event = new LogDTO(
//            UUID.randomUUID(),
//            Instant.now(),
//            level,
//            message,
//            serviceName,
//            context,
//            spanContext.isValid() ? spanContext.getTraceId() : null,
//            spanContext.isValid() ? spanContext.getSpanId() : null,
//            TraceContextUtil.getUsernameOrNull(),
//            appProperties.getEnv()
//        );
//
//        sendKafkaEvent(TOPIC_LOG_STREAM_LOG_INVOICE, event, "log");
//    }

    /**
     * Versendet ein Kafka-Event zur Bestätigungsmail beim Erstellen einer Person.
     *
     * @param invoice die erstellte Person
     * @param role   die zugewiesene Rolle
     */
//    @Observed(name = "kafka-publisher.send-mail")
//    public void sendMail(Invoice invoice, String role) {
//        final var mailDTO = SendMail.fromEntity(invoice);
//        sendKafkaEvent(TOPIC_NOTIFICATION_ACCOUNT_CREATED, mailDTO, "sendMail");
//    }


    /**
     * Zentraler Kafka-Versand mit OpenTelemetry-Span.
     *
     * @param topic     Ziel-Topic
     * @param payload   Event-Inhalt (DTO oder String)
     * @param operation Name der Aktion, z.B. 'createAccount'
     */
//    private void sendKafkaEvent(String topic, Object payload, String operation) {
//        Span kafkaSpan = tracer.spanBuilder(String.format("kafka-publisher.%s", topic))
//            .setParent(Context.current())
//            .setAttribute("messaging.system", "kafka")
//            .setAttribute("messaging.destination", topic)
//            .setAttribute("messaging.destination_kind", "topic")
//            .setAttribute("messaging.operation", operation)
//            .startSpan();
//        try (Scope scope = kafkaSpan.makeCurrent()) {
//            assert scope != null;
//            SpanContext spanContext = kafkaSpan.getSpanContext();
//            //SpanContext spanContext = Span.current().getSpanContext();
//
//            Headers headers = kafkaUtilService.buildStandardHeaders(topic, operation, spanContext);
//
//            ProducerRecord<String, Object> record = new ProducerRecord<>(topic, null, null, null, payload, headers);
//            kafkaTemplate.send(record);
//
//            kafkaSpan.setAttribute("messaging.kafka.message_type", payload.getClass().getSimpleName());
//        } catch (Exception e) {
//            kafkaSpan.recordException(e);
//            kafkaSpan.setStatus(StatusCode.ERROR, "Kafka send failed");
//            log.error("❌ Kafka send failed: topic={}, payload={}", topic, payload, e);
//        } finally {
//            kafkaSpan.end();
//        }
//
//        log.info("📤 Kafka-Event '{}' an Topic '{}': {}", operation, topic, payload);
//    }
}
