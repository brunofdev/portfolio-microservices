package com.mailservice.service;

import com.mailservice.dto.FeedbackDTO;
import com.mailservice.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private final WebClient.Builder webClientBuilder;

    @Value("${brevo.api.key}") // Lida a partir do application.properties
    private String apiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public void sendWelcomeEmail(UserDTO user) {
        WebClient webClient = webClientBuilder.baseUrl("https://api.brevo.com/v3").build();

        String subject = "Seja bem-vindo(a), " + user.getNome() + "!";
        // Você pode criar um template HTML mais elaborado aqui
        String htmlContent = "<html><body><h1>Olá " + user.getNome() + "!</h1><p>Seu cadastro foi realizado com sucesso. Estamos felizes em ter você conosco!</p></body></html>";

        // O corpo da requisição no formato que a API do Brevo espera
        Map<String, Object> body = Map.of(
                "sender", Map.of("email", senderEmail, "name", "Bruno Fraga Dev"),
                "to", List.of(Map.of("email", user.getEmail(), "name", user.getNome())),
                "subject", subject,
                "htmlContent", htmlContent
        );
        // Faz a chamada POST para a API do Brevo
        webClient.post()
                .uri("/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve() // Executa a requisição
                .toBodilessEntity() // Não nos importamos com o corpo da resposta de sucesso, apenas que deu 2xx
                .block(); // Executa a chamada de forma síncrona

        System.out.println("E-mail de boas-vindas enviado para " + user.getEmail() + " via API Brevo.");
    }
    /**
     * NOVO MÉTODO:
     * Envia um e-mail de notificação para o administrador (você) sobre um novo feedback recebido.
     * @param feedback O DTO do feedback recebido da fila do RabbitMQ.
     */
    public void sendNewFeedbackNotification(FeedbackDTO feedback) {
        // 1. Prepara o WebClient para se comunicar com a API do Brevo
        WebClient webClient = webClientBuilder.baseUrl("https://api.brevo.com/v3").build();

        // 2. Define o conteúdo do e-mail de notificação
        String subject = "Novo Feedback Recebido no seu Portfólio!";
        // Usando Text Block (Java 15+) para um HTML mais legível
        String htmlContent = String.format("""
            <html><body>
                <h2>Olá Bruno, você recebeu um novo feedback!</h2>
                <p><strong>De:</strong> %s</p>
                <p><strong>Nota:</strong> %d de 5 estrelas</p>
                <p><strong>Comentário:</strong></p>
                <blockquote style="border-left: 2px solid #ccc; padding-left: 10px; margin-left: 5px;">
                    %s
                </blockquote>
                <p><strong>Recebido em:</strong> %s</p>
            </body></html>
            """,
                feedback.getUserName(),
                feedback.getUserRating(),
                feedback.getUserFeedback(),
                feedback.getCreatedAt().toString() // Pode ser formatado para ficar mais amigável
        );

        // 3. Monta o corpo da requisição para a API do Brevo
        Map<String, Object> body = Map.of(
                "sender", Map.of("email", senderEmail, "name", "Alerta de Feedback"),
                "to", List.of(Map.of("email", "brunofragaa97@gmail.com", "name", "Bruno Fraga (Admin)")),
                "subject", subject,
                "htmlContent", htmlContent
        );

        // 4. Faz a chamada POST para a API do Brevo
        webClient.post()
                .uri("/smtp/email")
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();

        System.out.println("E-mail de notificação de feedback enviado para o host.");
    }
}

