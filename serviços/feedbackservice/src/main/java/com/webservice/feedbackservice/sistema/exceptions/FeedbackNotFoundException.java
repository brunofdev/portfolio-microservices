package com.webservice.feedbackservice.sistema.exceptions;

public class FeedbackNotFoundException extends  RuntimeException {
    public FeedbackNotFoundException (String message){
        super(message);
    }
}
