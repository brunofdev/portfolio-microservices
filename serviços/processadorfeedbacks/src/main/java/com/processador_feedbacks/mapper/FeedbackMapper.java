package com.processador_feedbacks.mapper;

import com.processador_feedbacks.dto.FeedbackCreateDTO;
import com.processador_feedbacks.dto.FeedbackDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FeedbackMapper {

    public FeedbackMapper(){

    }
    public FeedbackDTO mapFeedbackCreateDtoToFeedbackDto(FeedbackCreateDTO feedbackCreateDTO, String userName){
        return new FeedbackDTO(
                feedbackCreateDTO.getUserFeedback(),
                feedbackCreateDTO.getUserRating(),
                LocalDateTime.now(),
                userName
        );
    }
}
