package com.processador_feedbacks.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class FeedbackCreateDTO {
        @NotNull(message = "Não pode ser vazio")
        @NotBlank(message = "o feedback Não pode ser vazio")
        private String userFeedback;
        @NotNull(message = "A nota é obrigatória")
        @Min(value = 1, message = "A nota não pode ser 0 ou negativa")
        private int userRating;



    public String getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(String userFeedback) {
        this.userFeedback = userFeedback;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        this.userRating = userRating;
    }
}

