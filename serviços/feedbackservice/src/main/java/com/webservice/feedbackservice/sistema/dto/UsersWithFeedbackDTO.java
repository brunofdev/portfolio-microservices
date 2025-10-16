package com.webservice.feedbackservice.sistema.dto;

import com.webservice.feedbackservice.sistema.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersWithFeedbackDTO {
    private Long id;
    private String feedback;
    private Integer userRating;
    private LocalDateTime time;
    private String userName;
    private String name;
    private UserRole userRole;

}
