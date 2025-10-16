package com.user_service.messaging.producer;

import com.user_service.dto.UserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserFeedbackProducer {
    private static final String queue1 = "user-deleted-feedback-cleanup.queue";

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public void sendToQueueFeedbackCleanup(UserDTO userDTO){
        System.out.println("Enviando para a fila :::" + queue1 + ":::");
        rabbitTemplate.convertAndSend(queue1, userDTO);
        System.out.println("Mensagem enviada para a fila corretamente");

    }
}
