package com.webservice.feedbackservice.sistema.messaging.consumer;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.UserDTO;
import com.webservice.feedbackservice.sistema.service.FeedbackService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackConsumer {
    private static final String QUEUE1 = "feedback-create.queue";
    private static final String QUEUE2 = "user-deleted-feedback-cleanup.queue";
    @Autowired
    private FeedbackService feedbackService;

    @RabbitListener(queues = QUEUE1)
    public void receiveMessage(FeedbackDTO feedbackDTO) {
       try {
           System.out.println("Mensagem recebida. Delegando para o serviço de processamento.");
           feedbackService.createNewFeedback(feedbackDTO);
       }catch (Exception e){
           System.out.println ("Erro ao processar mensagem: " + e.getMessage());
       }
    }
    @RabbitListener(queues = QUEUE2)
    public void receiveMessage(UserDTO userDTO) {
        try {
            System.out.println("Mensagem recebida. Delegando para o serviço de processamento.");
            feedbackService.deleteFeedbacksWithUser(userDTO);
            System.out.println("Mensagem processada.");
        }catch (Exception e){
            System.out.println ("Erro ao processar mensagem: " + e.getMessage());
        }
}
}

