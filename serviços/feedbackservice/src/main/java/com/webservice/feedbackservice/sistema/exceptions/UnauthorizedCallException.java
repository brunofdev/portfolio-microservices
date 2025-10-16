package com.webservice.feedbackservice.sistema.exceptions;

public class UnauthorizedCallException extends RuntimeException{
    public UnauthorizedCallException(String message){
        super(message);
    }
}
