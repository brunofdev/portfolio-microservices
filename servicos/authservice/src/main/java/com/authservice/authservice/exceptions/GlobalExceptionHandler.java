package com.authservice.authservice.exceptions;

import com.authservice.authservice.dto.apiresponse.ApiError;
import com.authservice.authservice.dto.apiresponse.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handler específico para a exceção que o AuthenticationService lança
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    // Handler para erros de validação do @Valid (ex: campos em branco no login)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Pega a primeira mensagem de erro de validação para ser mais simples
        String errorMessage = "Dados de entrada inválidos.";
        if (ex.getBindingResult().hasFieldErrors()) {
            errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", errorMessage);
    }

    // Handler genérico para qualquer outra exceção não esperada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Ocorreu uma exceção não tratada no auth-service: ", ex);
        String message = "Ocorreu um erro inesperado no servidor.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }

    // Método privado para construir a resposta de erro padronizada
    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(status.value());
        apiError.setError(error);
        apiError.setMessage(message);
        apiError.setTimestamp(LocalDateTime.now());

        ApiResponse<Object> response = ApiResponse.error("Falha na autenticação.", apiError);
        return ResponseEntity.status(status).body(response);
    }
}