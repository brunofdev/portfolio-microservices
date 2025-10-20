package com.apigateway.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    /*
    essa classe "constroi" os direcionamento de forma clara e correta, sem ela é como se o gateway
    não soubesse para onde enviar as requisições
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Servico de Autenticacao
                .route("auth_service_route", r -> r
                                .path("/api/auth/**")
                                .uri("lb://auth-service")
                )
                // Servico de Usuarios
                .route("user_service_route", r -> r
                                .path("/api/users/**")
                                .uri("lb://user-service")
                )
                // Servico de Process Feedback
                .route("processfeedback_service_route", r -> r
                                .path("/api/processfeedback/**")
                                .uri("lb://processador-feedbacks")
                )
                // Servico de Feedback
                .route("feedback_service_route", r -> r
                                .path("/api/feedback/**")
                                .uri("lb://feedback-service")
                )
                .build();
    }
}