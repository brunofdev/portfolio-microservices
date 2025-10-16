package com.webservice.feedbackservice.sistema.exceptions;

public class UserNameIsEmptyException extends RuntimeException{
    public UserNameIsEmptyException (String message){
        super(message);
    }
}
