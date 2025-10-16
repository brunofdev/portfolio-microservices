package com.apigateway.core;

import com.apigateway.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${service.auth.url}")
    private String authServiceUrl;

    @Value("${service.user.url}")
    private String userServiceUrl;

    // CORREÇÃO 1: Adicionada a chave de fechamento }
    @Value("${service.feedback.url}")
    private String feedbackServiceUrl;

    @Value("${service.processfeedback.url}")
    private String processfeedbackUrl;

    private final AuthenticationFilter authenticationFilter;

    public GatewayConfig(AuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Rota para o Serviço de Autenticação
                .route("auth_service_route", route -> route
                        .path("/api/auth/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(authServiceUrl))

                // Rota para o Serviço de Usuários
                .route("user_service_route", route -> route
                        .path("/api/users/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(userServiceUrl))

                // Rota para o Serviço de Feedback(apenas para criar feedbacks)
                .route("processfeedback_service_route", route -> route
                        .path("/api/processfeedback/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(processfeedbackUrl))
                // Rota para o Serviço de Feedback(restante dos serviços e verbos como get, delete etc... de feedback
                .route("feedback_service_route", route -> route
                        .path("/api/feedback/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri(feedbackServiceUrl))
                .build();

    }
}