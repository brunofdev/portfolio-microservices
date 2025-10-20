package com.webservice.feedbackservice.sistema.core;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * Este Bean cria um "construtor" de WebClient que é
     * "inteligente" e entende o Service Discovery (Eureka).
     * A anotacao @LoadBalanced é a magica aqui.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}