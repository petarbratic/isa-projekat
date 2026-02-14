package rs.ac.ftn.isa.uploadapp.consumer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import rs.ac.ftn.isa.uploadapp.config.RabbitMqConfig;
import rs.ac.ftn.isa.uploadapp.dto.UploadEvent;
import rs.ac.ftn.isa.uploadapp.proto.UploadEventProto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nova aplikacija – prima poruku svaki put kada se na Jutjubiću (isa-backend) objavi novi video.
 * Prima i JSON i Protobuf poruke.
 */
@Component
public class UploadEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(UploadEventConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.UPLOAD_EVENT_JSON_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void onJsonMessage(@Payload UploadEvent event) {
        if (event != null) {
            log.info("[NOVA APLIKACIJA – JSON] Novi video: id={}, naziv='{}', veličina={} B, autor='{}', createdAt='{}'",
                    event.getVideoId(), event.getTitle(), event.getSizeBytes(),
                    event.getAuthorUsername(), event.getCreatedAt());
        }
    }

    @RabbitListener(queues = RabbitMqConfig.UPLOAD_EVENT_PROTOBUF_QUEUE)
    public void onProtobufMessage(Message amqpMessage) {
        try {
            UploadEventProto.UploadEvent proto = UploadEventProto.UploadEvent.parseFrom(amqpMessage.getBody());
            log.info("[NOVA APLIKACIJA – Protobuf] Novi video: id={}, naziv='{}', veličina={} B, autor='{}', createdAt='{}'",
                    proto.getVideoId(), proto.getTitle(), proto.getSizeBytes(),
                    proto.getAuthorUsername(), proto.getCreatedAt());
        } catch (Exception e) {
            log.error("Greška pri čitanju Protobuf poruke", e);
        }
    }
}
