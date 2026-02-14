package rs.ac.ftn.isa.backend.service.uploadevent;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.config.UploadEventMqConfig;
import rs.ac.ftn.isa.backend.dto.UploadEvent;
import rs.ac.ftn.isa.backend.proto.UploadEventProto;

@Service
public class UploadEventProducer {

    private static final String CONTENT_TYPE_PROTOBUF = "application/x-protobuf";

    private final RabbitTemplate uploadEventJsonRabbitTemplate;
    private final RabbitTemplate rabbitTemplate; // for raw send (Protobuf bytes)

    public UploadEventProducer(
            @Qualifier("uploadEventJsonRabbitTemplate") RabbitTemplate uploadEventJsonRabbitTemplate,
            RabbitTemplate rabbitTemplate) {
        this.uploadEventJsonRabbitTemplate = uploadEventJsonRabbitTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Sends UploadEvent as JSON to the upload event exchange (JSON queue). */
    public void sendJson(UploadEvent event) {
        uploadEventJsonRabbitTemplate.convertAndSend(
                UploadEventMqConfig.UPLOAD_EVENT_EXCHANGE,
                UploadEventMqConfig.UPLOAD_EVENT_JSON_ROUTING_KEY,
                event
        );
    }

    /** Sends copy as JSON to inspect queue (nema consumera – vidiš u Get messages). */
    public void sendToInspectQueue(UploadEvent event) {
        uploadEventJsonRabbitTemplate.convertAndSend(
                UploadEventMqConfig.UPLOAD_EVENT_EXCHANGE,
                UploadEventMqConfig.UPLOAD_EVENT_INSPECT_ROUTING_KEY,
                event
        );
    }

    /** Sends copy as Protobuf to inspect queue (nema consumera – vidiš u Get messages, Encoding: Base64). */
    public void sendToInspectProtobufQueue(UploadEvent event) {
        UploadEventProto.UploadEvent proto = toProto(event);
        byte[] bytes = proto.toByteArray();
        MessageProperties props = new MessageProperties();
        props.setContentType(CONTENT_TYPE_PROTOBUF);
        props.setContentLength(bytes.length);
        Message message = new Message(bytes, props);
        rabbitTemplate.send(
                UploadEventMqConfig.UPLOAD_EVENT_EXCHANGE,
                UploadEventMqConfig.UPLOAD_EVENT_INSPECT_PROTOBUF_ROUTING_KEY,
                message
        );
    }

    /** Sends UploadEvent as Protobuf bytes to the upload event exchange (Protobuf queue). */
    public void sendProtobuf(UploadEvent event) {
        UploadEventProto.UploadEvent proto = toProto(event);
        byte[] bytes = proto.toByteArray();
        MessageProperties props = new MessageProperties();
        props.setContentType(CONTENT_TYPE_PROTOBUF);
        props.setContentLength(bytes.length);
        Message message = new Message(bytes, props);
        rabbitTemplate.send(
                UploadEventMqConfig.UPLOAD_EVENT_EXCHANGE,
                UploadEventMqConfig.UPLOAD_EVENT_PROTOBUF_ROUTING_KEY,
                message
        );
    }

    public static UploadEventProto.UploadEvent toProto(UploadEvent dto) {
        if (dto == null) return UploadEventProto.UploadEvent.getDefaultInstance();
        return UploadEventProto.UploadEvent.newBuilder()
                .setVideoId(dto.getVideoId() != null ? dto.getVideoId() : 0L)
                .setTitle(dto.getTitle() != null ? dto.getTitle() : "")
                .setSizeBytes(dto.getSizeBytes())
                .setAuthorId(dto.getAuthorId() != null ? dto.getAuthorId() : "")
                .setAuthorUsername(dto.getAuthorUsername() != null ? dto.getAuthorUsername() : "")
                .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : "")
                .build();
    }

    public static UploadEvent fromProto(UploadEventProto.UploadEvent proto) {
        if (proto == null) return null;
        UploadEvent dto = new UploadEvent();
        dto.setVideoId(proto.getVideoId());
        dto.setTitle(proto.getTitle());
        dto.setSizeBytes(proto.getSizeBytes());
        dto.setAuthorId(proto.getAuthorId());
        dto.setAuthorUsername(proto.getAuthorUsername());
        dto.setCreatedAt(proto.getCreatedAt());
        return dto;
    }
}
