package com.webservice.feedbackservice.sistema.exceptions;
import com.webservice.feedbackservice.sistema.dto.apiresponse.ApiError;
import com.webservice.feedbackservice.sistema.dto.apiresponse.ApiResponse;
import org.modelmapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = "Erro de validação nos dados de entrada.";
        if (ex.getBindingResult().hasFieldErrors()) {
            errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Failed", errorMessage);
    }
    @ExceptionHandler(UserDatailsNotFoundExcpetion.class)
    public ResponseEntity<ApiResponse<Object>> handleUserDetailsNotFound(UserDatailsNotFoundExcpetion ex) {
        logger.error("Dados de usuário não encontrados: ", ex);
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "User Data Unavailable", ex.getMessage());
    }
    @ExceptionHandler(ApiResponseIsEmpty.class)
    public ResponseEntity<ApiResponse<Object>> handleApiResponseIsEmpty(ApiResponseIsEmpty ex) {
        logger.error("A resposta da api do user-service esta vazia ", ex);
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "ApiResponse from user-service Data is Empty", ex.getMessage());
    }
    @ExceptionHandler({org.springframework.web.reactive.function.client.WebClientResponseException.class})
    public ResponseEntity<ApiResponse<Object>> handleWebClientResponseException(Exception ex) {
        logger.error("Erro na resposta do WebClient: ", ex);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, "External Service Error", ex.getMessage());
    }

    @ExceptionHandler({org.springframework.web.reactive.function.client.WebClientRequestException.class})
    public ResponseEntity<ApiResponse<Object>> handleWebClientRequestException(Exception ex) {
        logger.error("Erro de requisição ao WebClient: ", ex);
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, "External Service Request Error", ex.getMessage());
    }

    @ExceptionHandler(org.modelmapper.MappingException.class)
    public ResponseEntity<ApiResponse<Object>> handleMappingException(MappingException ex) {
        logger.error("Erro de mapeamento: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Mapping Error", ex.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex) {
        logger.error("Erro de acesso a dados: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Ocorreu uma exceção não tratada no feedback-service: ", ex);
        String message = "Ocorreu um erro inesperado no servidor.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }
    private ResponseEntity<ApiResponse<Object>> buildErrorResponse(HttpStatus status, String error, String message) {
        ApiError apiError = new ApiError();
        apiError.setStatus(status.value());
        apiError.setError(error);
        apiError.setMessage(message);
        apiError.setTimestamp(LocalDateTime.now());
        ApiResponse<Object> response = ApiResponse.error(message, apiError);
        return new ResponseEntity<>(response, status);
    }
}
