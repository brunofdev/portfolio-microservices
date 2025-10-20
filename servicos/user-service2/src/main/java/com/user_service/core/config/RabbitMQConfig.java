package com.user_service.core.config;


import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.amqp.core.Queue;

@Configuration
public class RabbitMQConfig {


    public static final String QUEUE1 = "user.welcome.email";
    public static final String QUEUE2 = "user.notify.posted.feedback.email.queue";
    public static final String QUEUE3 = "user-deleted-feedback-cleanup.queue";

    @Bean
    public Queue welcomeEmailQueue() {
        return new Queue(QUEUE1, true);
    }

    @Bean
    public Queue notifyPostedFeedbackQueue(){ return new Queue(QUEUE2, true); }

    @Bean
    public Queue userDeletedQueue() {
        return new Queue(QUEUE3, true);
    }
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
