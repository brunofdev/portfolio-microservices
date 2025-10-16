package com.user_service.exceptions;

import com.user_service.dto.apiresponse.ApiError;   // Importe suas classes
import com.user_service.dto.apiresponse.ApiResponse; // Importe suas classes
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

    // Trata exceções de usuário não encontrado
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User Not Found", ex.getMessage());
    }

    // Trata credenciais inválidas

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Credentials", ex.getMessage());
    }
    // Trata email já existente
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameAlreadyExists(EmailAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Email", ex.getMessage());
    }

    // Trata username já existente
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid UserName", ex.getMessage());
    }
    // Trata username já existente
    @ExceptionHandler(UserDontFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserDontFound(UserDontFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "User dont found by id", ex.getMessage());
    }

    // Trata erros de validação do @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        String errorMessage = "Erro de validação nos dados de entrada.";

        if (ex.getBindingResult().hasFieldErrors()) {
            errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }

        ApiError apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setError("Validation Failed");
        apiError.setMessage(errorMessage);
        apiError.setTimestamp(LocalDateTime.now());

        ApiResponse<Object> response = ApiResponse.error("Dados de entrada inválidos.", apiError);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Fallback para qualquer outra exceção não tratada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Ocorreu uma exceção não tratada: ", ex);
        String message = "Ocorreu um erro inesperado no servidor. Por favor, contate o suporte.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }

    // --- METODO PRIVADO AUXILIAR PARA CRIAR RESPOSTAS DE ERRO PADRONIZADAS ---
    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(status.value());
        apiError.setError(error);
        apiError.setMessage(message);
        apiError.setTimestamp(LocalDateTime.now());

        ApiResponse<Object> response = ApiResponse.error("Erro ao processar a requisição.", apiError);
        return ResponseEntity.status(status).body(response);
    }
}