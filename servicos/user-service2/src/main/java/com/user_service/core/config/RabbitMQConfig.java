package com.user_service.core.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
@Configuration
public class RabbitMQConfig {


    public static final String QUEUE_WELCOME_EMAIL = "user.welcome.email";

    public static final String QUEUE_USER_DELETED = "user-deleted-feedback-cleanup.queue";

    @Bean
    public Queue welcomeEmailQueue() {
        return new Queue(QUEUE_WELCOME_EMAIL, true);
    }


    @Bean
    public Queue userDeletedQueue() {
        return new Queue(QUEUE_USER_DELETED, true);
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
