package com.mailservice.messaging.consumer;

import com.mailservice.dto.FeedbackDTO;
import com.mailservice.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackCreateEmail {

    private static final String QUEUE_NAME = "feedback.created.email.queue";

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage (FeedbackDTO feedbackDTO){
        try {
            System.out.println("Mensagem recebida. Delegando para o serviço de processamento.");
            emailService.sendNewFeedbackNotification(feedbackDTO);// Delega o processamento
        }catch (Exception e){
            System.out.println ("Erro ao processar mensagem: " + e.getMessage());
        }
    }
}
