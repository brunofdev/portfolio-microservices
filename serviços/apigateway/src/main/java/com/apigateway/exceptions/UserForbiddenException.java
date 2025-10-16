package com.apigateway.exceptions;

public class UserForbiddenException extends RuntimeException{
    public UserForbiddenException (String message){
        super(message);
    }
}
