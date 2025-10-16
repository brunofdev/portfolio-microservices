package com.webservice.feedbackservice.sistema.core;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nomes originais das filas
    private static final String QUEUE1_NAME = "feedback-create.queue";
    private static final String QUEUE2_NAME = "feedback.created.email.queue";

    /**
     * Fila para criação de feedback
     */
    @Bean
    public Queue feedbackCreateQueue() {
        return new Queue(QUEUE1_NAME, true); // durável
    }

    /**
     * Fila para envio de e-mail de feedback
     */
    @Bean
    public Queue feedbackEmailQueue() {
        return new Queue(QUEUE2_NAME, true); // durável
    }

    /**
     * Conversor de mensagens JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
