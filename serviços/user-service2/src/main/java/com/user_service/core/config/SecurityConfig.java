package com.user_service.core.config;

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
        // Ao definir este bean, você desativa a segurança padrão do Spring
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Libera os endpoints que devem ser públicos
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/internal/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}