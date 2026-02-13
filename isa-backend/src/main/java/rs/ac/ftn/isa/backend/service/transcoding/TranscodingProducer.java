package rs.ac.ftn.isa.backend.service.transcoding;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.config.TranscodingMqConfig;
import rs.ac.ftn.isa.backend.dto.TranscodeJobMessage;

@Service
public class TranscodingProducer {

    private final RabbitTemplate rabbitTemplate;

    public TranscodingProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(TranscodeJobMessage msg) {
        rabbitTemplate.convertAndSend(
                TranscodingMqConfig.TRANSCODE_EXCHANGE,
                TranscodingMqConfig.TRANSCODE_ROUTING_KEY,
                msg
        );
    }
}
