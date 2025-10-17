package com.apigateway.core;

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

    @Value("${service.feedback.url}")
    private String feedbackServiceUrl;

    @Value("${service.processfeedback.url}")
    private String processfeedbackUrl;

    // 1. REMOVA A INJECAO DO FILTRO
    //    private final AuthenticationFilter authenticationFilter;
    //
    //    public GatewayConfig(AuthenticationFilter authenticationFilter) {
    //        this.authenticationFilter = authenticationFilter;
    //    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Servico de Autenticacao
                .route("auth_service_route", r -> r
                                .path("/api/auth/**")
                                .uri(authServiceUrl)
                        // 2. REMOVA O .filters() DAQUI
                )
                // Servico de Usuarios
                .route("user_service_route", r -> r
                                .path("/api/users/**")
                                .uri(userServiceUrl)
                        // 3. REMOVA O .filters() DAQUI
                )
                // Servico de Process Feedback
                .route("processfeedback_service_route", r -> r
                                .path("/api/processfeedback/**")
                                .uri(processfeedbackUrl)
                        // 4. REMOVA O .filters() DAQUI
                )
                // Servico de Feedback
                .route("feedback_service_route", r -> r
                                .path("/api/feedback/**")
                                .uri(feedbackServiceUrl)
                        // 5. REMOVA O .filters() DAQUI
                )
                // 6. REMOVA A ROTA DO PROMETHEUS DAQUI
                .build();
    }
}