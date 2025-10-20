package com.user_service.exceptions;

public class UserDontHaveEmailRegistered extends RuntimeException{
    public UserDontHaveEmailRegistered(String message){
        super(message);
    }
}
