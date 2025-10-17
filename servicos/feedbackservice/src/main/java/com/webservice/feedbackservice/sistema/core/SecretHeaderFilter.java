package com.webservice.feedbackservice.sistema.core;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(1) // Garante que este filtro execute ANTES do Spring Security
public class SecretHeaderFilter implements Filter {

    @Value("${api.internal.secret}")
    private String internalApiSecret;

    // A UNICA lista de excecoes: rotas de monitoramento
    private static final List<String> ACTUATOR_PATHS = List.of(
            "/actuator"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // 1. Se for um endpoint do Actuator (Prometheus), DEIXA PASSAR.
        if (isActuatorPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Para TODAS AS OUTRAS rotas (publicas ou protegidas),
        // EXIGE o header secreto.
        String secretHeader = httpRequest.getHeader("X-Internal-Secret");
        if (internalApiSecret != null && internalApiSecret.equals(secretHeader)) {
            // A chamada veio do Gateway. E confiavel.
            chain.doFilter(request, response);
        } else {
            // A chamada veio DIRETAMENTE. BLOQUEIA.
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Acesso direto nao permitido.");
        }
    }

    private boolean isActuatorPath(String path) {
        return ACTUATOR_PATHS.stream().anyMatch(path::startsWith);
    }
}