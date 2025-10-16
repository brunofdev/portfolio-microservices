package com.processador_feedbacks.producer;

import com.processador_feedbacks.dto.FeedbackDTO;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class FeedbackProducer {
    private static final String queue_name = "feedback-create.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(FeedbackDTO feedbackDTO) {
        System.out.println("Enviando mensagem para a fila '" + queue_name + "'...");
        rabbitTemplate.convertAndSend(queue_name, feedbackDTO);
        System.out.println("Mensagem enviada com sucesso!");
    }
}
