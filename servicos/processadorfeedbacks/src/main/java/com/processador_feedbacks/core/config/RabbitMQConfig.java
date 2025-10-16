package com.processador_feedbacks.core.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String QUEUE_NAME = "feedback-create.queue";

    @Bean
    public Queue queue() {
        // Declara a fila 'feedback.queue' como durável
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        // Configura o conversor para JSON, para que o RabbitTemplate
        // possa serializar objetos Java para o formato JSON.
        return new Jackson2JsonMessageConverter();
    }
}