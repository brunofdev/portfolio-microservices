package com.mailservice.messaging.consumer;

import com.mailservice.dto.FeedbackDTO;
import com.mailservice.dto.UserDTO;
import com.mailservice.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackCreateEmail {

    private static final String QUEUE1 = "feedback.created.email.queue";
    private static final String QUEUE2 = "user.notify.posted.feedback.email.queue";

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = QUEUE1)
    public void receiveMessage (FeedbackDTO feedbackDTO){
        try {
            System.out.println("Mensagem recebida. Delegando para o serviço de processamento.");
            emailService.sendNewFeedbackNotificationToHost(feedbackDTO);
        }catch (Exception e){
            System.out.println ("Erro ao processar mensagem: " + e.getMessage());
        }
    }
    @RabbitListener(queues = QUEUE2)
    public void receiveMessage (UserDTO userDTO) {
        try {
            System.out.println("Mensagem recebida. Delegando para o serviço de processamento.");
            emailService.sendNewFeedbackNotificationToUser(userDTO);
            System.out.println("E-mail de notificação de feedback enviado para o host.");
        } catch (Exception e) {
            System.out.println("Erro ao processar mensagem: " + e.getMessage());
        }
    }
}
