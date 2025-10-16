package com.webservice.feedbackservice.sistema.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;


import java.time.LocalDateTime;

@Entity
@Table(name = "user_feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Não pode ser vazio")
    @NotBlank(message = "o feedback Não pode ser vazio")
    private String userFeedback ;
    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota não pode ser 0 ou negativa")
    private int userRating;
    @NotNull(message = "O horário do feedback é obrigatório")
    @PastOrPresent(message = "O horário não pode ser no futuro")
    @Column(name = "time")
    private LocalDateTime createdAt;
    @NotNull(message = "Username deve ser preenchido para criar feedbacks")
    @Column(name = "user_name")
    private String userName;

    public Feedback(){};
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getUserName() {
        return userName.toUpperCase();
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
