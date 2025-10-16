package com.mailservice.messaging.consumer;

import com.mailservice.dto.UserDTO;
import com.mailservice.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailWelcomeConsumer {
    private static final String QUEUE_NAME = "email-welcome.queue";

    @Autowired
    private EmailService emailService; // Injeta o serviço

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveWelcomeEmailMessage(UserDTO user) {
        System.out.println("Recebida mensagem para enviar boas-vindas para: " + user.getEmail());
        emailService.sendWelcomeEmail(user);
    }
}
