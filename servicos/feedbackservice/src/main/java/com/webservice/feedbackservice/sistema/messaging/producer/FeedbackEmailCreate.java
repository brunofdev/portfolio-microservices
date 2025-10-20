package com.webservice.feedbackservice.sistema.messaging.producer;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEmailCreate {

    private static final String queue_newFeedbackNotify = "feedback.created.email.queue";
    private static final String queue_notifyUser = "feedback.notify.user.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToQueueRabbit(FeedbackDTO feedbackDTO){
        System.out.println("Enviando mensagem para a fila '" + queue_newFeedbackNotify + "'... e fila "
                + queue_notifyUser);
        rabbitTemplate.convertAndSend(queue_newFeedbackNotify, feedbackDTO);
        rabbitTemplate.convertAndSend(queue_notifyUser, feedbackDTO);
        System.out.println("Mensagens enviadas com sucesso!");
    }

}
