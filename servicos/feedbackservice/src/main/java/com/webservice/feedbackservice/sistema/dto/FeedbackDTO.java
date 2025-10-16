package com.webservice.feedbackservice.sistema.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO implements Serializable {
    private String userFeedback;
    private int userRating;
    private LocalDateTime createdAt;
    private String userName;

    public String getUserName() {
        return userName.toUpperCase();
    }
}

