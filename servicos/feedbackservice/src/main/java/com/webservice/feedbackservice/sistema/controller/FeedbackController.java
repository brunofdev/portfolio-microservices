package com.webservice.feedbackservice.sistema.controller;

import com.webservice.feedbackservice.sistema.dto.FeedbackCreateDTO;
import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.UsersWithFeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.apiresponse.ApiResponse;
import com.webservice.feedbackservice.sistema.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    private String urlUserService;
    public FeedbackController (FeedbackService feedbackService){
        this.feedbackService = feedbackService;
    }
    //este endpoint não precisa de token jwt, restrição configurada no apigateway
    @GetMapping("/getallfeedbacks")
    public ResponseEntity<ApiResponse<List<UsersWithFeedbackDTO>>> listAllFeedbacks(){
        return ResponseEntity.ok().body(ApiResponse.success("Recurso Obtido", feedbackService.listAllWithUserDetails()));
    }

    @DeleteMapping("/deletefeedback/{id}")
    public ResponseEntity<ApiResponse> deleteFeedback(@PathVariable Long id){
        feedbackService.deleteFeedback(id);
        return ResponseEntity.ok(ApiResponse.success("Recurso removido", null));
    }

}