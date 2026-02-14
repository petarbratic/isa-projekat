package rs.ac.ftn.isa.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UploadEventMqConfig {

    public static final String UPLOAD_EVENT_EXCHANGE = "upload.event.exchange";
    public static final String UPLOAD_EVENT_JSON_QUEUE = "upload.event.json.queue";
    public static final String UPLOAD_EVENT_PROTOBUF_QUEUE = "upload.event.protobuf.queue";
    /** Queue za pregled JSON u RabbitMQ UI – nema consumera. */
    public static final String UPLOAD_EVENT_INSPECT_QUEUE = "upload.event.inspect.queue";
    /** Queue za pregled Protobuf u RabbitMQ UI – nema consumera. */
    public static final String UPLOAD_EVENT_INSPECT_PROTOBUF_QUEUE = "upload.event.inspect.protobuf.queue";
    public static final String UPLOAD_EVENT_JSON_ROUTING_KEY = "upload.event.json";
    public static final String UPLOAD_EVENT_PROTOBUF_ROUTING_KEY = "upload.event.protobuf";
    public static final String UPLOAD_EVENT_INSPECT_ROUTING_KEY = "upload.event.inspect";
    public static final String UPLOAD_EVENT_INSPECT_PROTOBUF_ROUTING_KEY = "upload.event.inspect.protobuf";

    @Bean
    public DirectExchange uploadEventExchange() {
        return new DirectExchange(UPLOAD_EVENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue uploadEventJsonQueue() {
        return QueueBuilder.durable(UPLOAD_EVENT_JSON_QUEUE).build();
    }

    @Bean
    public Queue uploadEventProtobufQueue() {
        return QueueBuilder.durable(UPLOAD_EVENT_PROTOBUF_QUEUE).build();
    }

    @Bean
    public Queue uploadEventInspectQueue() {
        return QueueBuilder.durable(UPLOAD_EVENT_INSPECT_QUEUE).build();
    }

    @Bean
    public Queue uploadEventInspectProtobufQueue() {
        return QueueBuilder.durable(UPLOAD_EVENT_INSPECT_PROTOBUF_QUEUE).build();
    }

    @Bean
    public Binding uploadEventJsonBinding(Queue uploadEventJsonQueue, DirectExchange uploadEventExchange) {
        return BindingBuilder.bind(uploadEventJsonQueue).to(uploadEventExchange).with(UPLOAD_EVENT_JSON_ROUTING_KEY);
    }

    @Bean
    public Binding uploadEventProtobufBinding(Queue uploadEventProtobufQueue, DirectExchange uploadEventExchange) {
        return BindingBuilder.bind(uploadEventProtobufQueue).to(uploadEventExchange).with(UPLOAD_EVENT_PROTOBUF_ROUTING_KEY);
    }

    @Bean
    public Binding uploadEventInspectBinding(Queue uploadEventInspectQueue, DirectExchange uploadEventExchange) {
        return BindingBuilder.bind(uploadEventInspectQueue).to(uploadEventExchange).with(UPLOAD_EVENT_INSPECT_ROUTING_KEY);
    }

    @Bean
    public Binding uploadEventInspectProtobufBinding(Queue uploadEventInspectProtobufQueue, DirectExchange uploadEventExchange) {
        return BindingBuilder.bind(uploadEventInspectProtobufQueue).to(uploadEventExchange).with(UPLOAD_EVENT_INSPECT_PROTOBUF_ROUTING_KEY);
    }

    /** RabbitTemplate for sending UploadEvent as JSON. Uses shared Jackson converter. */
    @Bean(name = "uploadEventJsonRabbitTemplate")
    public RabbitTemplate uploadEventJsonRabbitTemplate(
            ConnectionFactory connectionFactory,
            @Qualifier("jacksonMessageConverter") MessageConverter jacksonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter);
        return template;
    }
}
