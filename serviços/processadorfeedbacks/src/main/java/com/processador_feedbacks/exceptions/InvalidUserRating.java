package com.processador_feedbacks.exceptions;

public class InvalidUserRating extends RuntimeException{
    public InvalidUserRating (String message){
        super(message);
    }
}
