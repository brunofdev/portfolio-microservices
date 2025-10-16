package com.processador_feedbacks.validation;

import com.processador_feedbacks.core.StringFormater;
import com.processador_feedbacks.dto.FeedbackCreateDTO;
import com.processador_feedbacks.exceptions.InvalidUserRating;
import com.processador_feedbacks.exceptions.ShortUserFeedback;
import com.processador_feedbacks.exceptions.UserNameIsEmptyException;
import org.springframework.stereotype.Component;

@Component
public class FeedbackValidation {

    public FeedbackValidation(){

    }
    public void validateFeedback(FeedbackCreateDTO dto, String userName){
        if (userName.trim().isBlank()){
            throw new UserNameIsEmptyException("O nome de usuario não pode ser vazio");
        }
        String textFeedback = StringFormater.removeSpaces(dto.getUserFeedback());
        if(textFeedback.length() <= 15){
            throw new ShortUserFeedback("O feedback enviado é muito curto");
        }
        if(dto.getUserRating() > 5){
            throw new InvalidUserRating("A nota máxima deve ser 5");
        }
    }
}
