package com.processador_feedbacks.exceptions;

public class UserNameIsEmptyException extends RuntimeException{
    public UserNameIsEmptyException(String message){
        super(message);
    }
}
