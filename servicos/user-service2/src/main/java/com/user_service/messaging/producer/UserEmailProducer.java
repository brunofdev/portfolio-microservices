package com.user_service.messaging.producer;

import com.user_service.dto.UserDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEmailProducer {
    private static final String queue1 = "email-welcome.queue";
    private static final String queue2 = "user.notify.posted.feedback.email.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendToQueueEmailWelcome(UserDTO userDTO) {
        System.out.println("Enviando mensagem para a fila '" + queue1 + "'...");
        rabbitTemplate.convertAndSend(queue1, userDTO);
        System.out.println("Mensagem enviada com sucesso!");
    }
    public void sendToQueueNotifyPostedFeedback(UserDTO userDTO){
        System.out.println("Enviando mensagem para a fila '" + queue2 + "'...");
        rabbitTemplate.convertAndSend(queue2, userDTO);
        System.out.println("Mensagem enviada com sucesso!");
    }
}
