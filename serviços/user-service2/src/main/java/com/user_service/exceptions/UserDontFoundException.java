package com.user_service.exceptions;

public class UserDontFoundException extends RuntimeException{
    public UserDontFoundException (String message){
        super(message);
    }
}
