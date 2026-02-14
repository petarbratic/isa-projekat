package rs.ac.ftn.isa.backend.service.uploadevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.config.UploadEventMqConfig;
import rs.ac.ftn.isa.backend.dto.UploadEvent;
import rs.ac.ftn.isa.backend.proto.UploadEventProto;

@Service
public class UploadEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UploadEventConsumer.class);

    /** Receives UploadEvent as JSON (Jackson-deserialized). */
    @RabbitListener(queues = UploadEventMqConfig.UPLOAD_EVENT_JSON_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleJson(UploadEvent event) {
        if (event != null) {
            logEvent("JSON", event);
        }
    }

    /** Receives raw message and deserializes Protobuf. */
    @RabbitListener(queues = UploadEventMqConfig.UPLOAD_EVENT_PROTOBUF_QUEUE)
    public void handleProtobuf(Message amqpMessage) {
        try {
            UploadEventProto.UploadEvent proto = UploadEventProto.UploadEvent.parseFrom(amqpMessage.getBody());
            UploadEvent event = UploadEventProducer.fromProto(proto);
            if (event != null) {
                logEvent("Protobuf", event);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Protobuf UploadEvent", e);
        }
    }

    private void logEvent(String format, UploadEvent event) {
        log.info("[UploadEvent {}] Novi video: videoId={}, naziv='{}', veličina={} bajtova, autorId={}, autorUsername='{}', createdAt='{}'",
                format,
                event.getVideoId(),
                event.getTitle(),
                event.getSizeBytes(),
                event.getAuthorId(),
                event.getAuthorUsername(),
                event.getCreatedAt());
    }
}
