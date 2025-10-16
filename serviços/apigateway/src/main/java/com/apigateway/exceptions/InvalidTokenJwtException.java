package com.apigateway.exceptions;

public class InvalidTokenJwtException extends RuntimeException{
    public InvalidTokenJwtException  (String message){
        super (message);
    }
}
