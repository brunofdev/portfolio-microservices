package com.webservice.feedbackservice.sistema.messaging.producer;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEmailCreate {

    private static final String queue_name = "feedback.created.email.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendToQueueRabbit(FeedbackDTO feedbackDTO){
        System.out.println("Enviando mensagem para a fila '" + queue_name + "'...");
        rabbitTemplate.convertAndSend(queue_name, feedbackDTO);
        System.out.println("Mensagem enviada com sucesso!");
    }

}
