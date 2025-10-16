package com.webservice.feedbackservice.sistema.exceptions;

public class ApiResponseIsEmpty extends RuntimeException {
    public ApiResponseIsEmpty (String message){
        super(message);
    }
}
