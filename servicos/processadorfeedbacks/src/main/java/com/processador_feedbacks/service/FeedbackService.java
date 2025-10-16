package com.processador_feedbacks.service;

import com.processador_feedbacks.dto.FeedbackCreateDTO;
import com.processador_feedbacks.dto.FeedbackDTO;
import com.processador_feedbacks.mapper.FeedbackMapper;
import com.processador_feedbacks.producer.FeedbackProducer;
import com.processador_feedbacks.validation.FeedbackValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.concurrent.RecursiveTask;


@Service
public class FeedbackService {

    private FeedbackProducer feedbackProducer;
    private FeedbackValidation feedbackValidation;
    private FeedbackMapper feedbackMapper;

    //injection
    @Autowired
    public FeedbackService(FeedbackProducer feedbackProducer,
                           FeedbackValidation feedbackValidation,
                           FeedbackMapper feedbackMapper){
        this.feedbackValidation = feedbackValidation;
        this.feedbackProducer = feedbackProducer;
        this.feedbackMapper = feedbackMapper;
    }
    //methods
    public FeedbackDTO processAndSendFeedback(FeedbackCreateDTO feedbackCreateDTO, String userName){
        feedbackValidation.validateFeedback(feedbackCreateDTO, userName);
        FeedbackDTO feedbackDTO = feedbackMapper.mapFeedbackCreateDtoToFeedbackDto(feedbackCreateDTO,userName);
        feedbackProducer.send(feedbackDTO);
        return feedbackDTO;
    }

}
