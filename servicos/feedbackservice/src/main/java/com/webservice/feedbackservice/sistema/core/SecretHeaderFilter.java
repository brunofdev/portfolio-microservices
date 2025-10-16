package com.webservice.feedbackservice.sistema.core;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1) // A anotação @Order(1) garante que este filtro execute ANTES de outros filtros do Spring
public class SecretHeaderFilter implements Filter {

    // Injeta o valor da chave secreta do seu arquivo application.properties
    // É uma prática melhor do que deixar a chave "hardcoded" no código.
    @Value("${api.internal.secret}")
    private String expectedSecret;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String secretHeader = httpRequest.getHeader("X-Internal-Secret");

        // Regra rigorosa: se o aperto de mão secreto estiver correto, pode passar.
        if (expectedSecret != null && expectedSecret.equals(secretHeader)) {
            chain.doFilter(request, response);
        } else {
            // Senão, bloqueia, não importa a URL.
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Acesso direto não permitido.");
        }
    }
}