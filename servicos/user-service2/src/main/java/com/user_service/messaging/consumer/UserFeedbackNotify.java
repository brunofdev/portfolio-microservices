package com.user_service.messaging.consumer;

import com.user_service.dto.FeedbackDTO;
import com.user_service.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFeedbackNotify {
    public static final String QUEUE1 = "feedback.notify.user.queue";

    @Autowired
    private UserService userService;

    @RabbitListener(queues = QUEUE1)
    public void receiveMessage(FeedbackDTO feedbackDTO){
        try {
            System.out.println("Mensagem recebida. Delegando para o serviço de processamento.");
            userService.sendEmailIfPostedFeedback(feedbackDTO);
        }catch (Exception e){
            System.out.println ("Erro ao processar mensagem: " + e.getMessage());
        }
    }
}

