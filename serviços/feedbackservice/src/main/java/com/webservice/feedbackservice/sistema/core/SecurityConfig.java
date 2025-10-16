package com.webservice.feedbackservice.sistema.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Diz ao Spring: "Esta é a configuração de segurança principal"
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Agora o Spring vai criar este bean porque a configuração é válida
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Deixa TODAS as rotas passarem pelo Spring Security,
                        // pois o SecretHeaderFilter já protege o serviço contra acesso externo.
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}