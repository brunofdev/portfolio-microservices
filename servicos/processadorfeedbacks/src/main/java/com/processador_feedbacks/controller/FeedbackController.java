package com.processador_feedbacks.controller;

import com.processador_feedbacks.dto.FeedbackCreateDTO;
import com.processador_feedbacks.dto.FeedbackDTO;
import com.processador_feedbacks.dto.apiresponse.ApiResponse;
import com.processador_feedbacks.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/processfeedback")
public class FeedbackController {
    private final FeedbackService feedbackService;
    public FeedbackController (FeedbackService feedbackService){
        this.feedbackService = feedbackService;
    }

    @PostMapping("/createfeedback")
    public ResponseEntity<ApiResponse<FeedbackDTO>> createFeedback (
                @RequestBody @Valid FeedbackCreateDTO feedBackCreateDTO,
                @RequestHeader("X-Authenticated-User") String username
    )
    {
        return ResponseEntity.ok()
                .body(ApiResponse.success("Recurso Criado com sucesso",
                        feedbackService.processAndSendFeedback(feedBackCreateDTO, username)));
    }
}