package com.webservice.feedbackservice.sistema.validation;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.apiresponse.ApiResponse;
import com.webservice.feedbackservice.sistema.exceptions.ApiResponseIsEmpty;
import com.webservice.feedbackservice.sistema.exceptions.UserNameIsEmptyException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FeedbackValidation {
    public void validationFeedback(FeedbackDTO feedbackDTO){

    }
    public void validateUserNames(List<String> userNames){
        if (userNames.isEmpty()) {
             throw new UserNameIsEmptyException("A lista de usernames está vazia");
        }
    }
    public void validateFeedback(FeedbackDTO dto) {
        if(dto.getUserName().trim().isBlank()){
            throw new UserNameIsEmptyException("Username não pode ser vazio");
        }
    }
    public void validateApiResponse(ApiResponse apiResponse){
        if (apiResponse == null || !apiResponse.getStatus()) {
            throw new ApiResponseIsEmpty("A lista de dados retornada pela Api está vazia");
        }
    }
}
