package com.processador_feedbacks.exceptions;

import com.processador_feedbacks.dto.apiresponse.ApiError;
import com.processador_feedbacks.dto.apiresponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortUserFeedback.class)
    public ResponseEntity<ApiResponse<Object>> handleShortUserFeedback(ShortUserFeedback ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    @ExceptionHandler(InvalidUserRating.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidUserRating(InvalidUserRating ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // Captura erros de validação de DTO (@NotNull, @NotBlank, @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Pega a mensagem de erro do primeiro campo que falhou na validação
        String errorMessage = "Erro de validação.";
        if (ex.getBindingResult().hasFieldErrors()) {
            errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", errorMessage);
    }

    // --- MÉTODO AUXILIAR PARA PADRONIZAR A CRIAÇÃO DE ERROS ---
    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(status.value());
        apiError.setError(error);
        apiError.setMessage(message);
        apiError.setTimestamp(LocalDateTime.now());

        // Usa a mensagem específica tanto no nível principal quanto no objeto de erro
        ApiResponse<Object> response = ApiResponse.error(message, apiError);

        return new ResponseEntity<>(response, status);
    }
}