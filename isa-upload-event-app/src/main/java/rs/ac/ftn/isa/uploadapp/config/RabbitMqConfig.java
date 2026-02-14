package rs.ac.ftn.isa.uploadapp.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguracija za isto exchange/queue kao isa-backend – ova aplikacija samo prima poruke.
 */
@Configuration
public class RabbitMqConfig {

    public static final String UPLOAD_EVENT_EXCHANGE = "upload.event.exchange";
    public static final String UPLOAD_EVENT_JSON_QUEUE = "upload.event.json.queue";
    public static final String UPLOAD_EVENT_PROTOBUF_QUEUE = "upload.event.protobuf.queue";
    public static final String UPLOAD_EVENT_JSON_ROUTING_KEY = "upload.event.json";
    public static final String UPLOAD_EVENT_PROTOBUF_ROUTING_KEY = "upload.event.protobuf";

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
    public Binding uploadEventJsonBinding(Queue uploadEventJsonQueue, DirectExchange uploadEventExchange) {
        return BindingBuilder.bind(uploadEventJsonQueue).to(uploadEventExchange).with(UPLOAD_EVENT_JSON_ROUTING_KEY);
    }

    @Bean
    public Binding uploadEventProtobufBinding(Queue uploadEventProtobufQueue, DirectExchange uploadEventExchange) {
        return BindingBuilder.bind(uploadEventProtobufQueue).to(uploadEventExchange).with(UPLOAD_EVENT_PROTOBUF_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }
}
