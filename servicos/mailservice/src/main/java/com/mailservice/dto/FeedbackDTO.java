package com.mailservice.dto;

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

