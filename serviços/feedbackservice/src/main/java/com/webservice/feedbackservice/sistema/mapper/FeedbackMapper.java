package com.webservice.feedbackservice.sistema.mapper;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.UserDTO;
import com.webservice.feedbackservice.sistema.dto.UsersWithFeedbackDTO;
import com.webservice.feedbackservice.sistema.entities.Feedback;
import com.webservice.feedbackservice.sistema.enums.UserRole;
import org.apache.catalina.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FeedbackMapper {
    public Map<String, UserDTO> mapListUserDTOtoMap (List<UserDTO>users){
        return users.stream().collect(Collectors.toMap(
                        user -> user.getUserName().toUpperCase(Locale.ROOT),
                        user -> user
        ));
    }
    public UsersWithFeedbackDTO mapUsersWithFeedbackDTO(String nome, UserRole userRole, Feedback feedback){
        return new UsersWithFeedbackDTO(
                feedback.getId(),
                feedback.getUserFeedback(),
                feedback.getUserRating(),
                feedback.getCreatedAt(),
                feedback.getUserName(),
                nome,
                userRole
        );
    }
    public List<UsersWithFeedbackDTO> mapUsersWithFeedbackDTO(List<UserDTO> usersDetails, List<Feedback> feedbacks)     {
        Map<String, UserDTO> userMapByUsername = mapListUserDTOtoMap(usersDetails);
        List<UsersWithFeedbackDTO> feedbacksWithUserDetails = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            String feedbackUsernameUpperCase = feedback.getUserName().toUpperCase(Locale.ROOT);
            UserDTO userFound = userMapByUsername.get(feedbackUsernameUpperCase);
            String nome = "Nome não encontrado";
            UserRole userRole = UserRole.USER;
            if (userFound != null) {
                nome = userFound.getNome();
                userRole = userFound.getUserRole() != null ? userFound.getUserRole() : UserRole.USER;
            }
            feedbacksWithUserDetails.add(
                    mapUsersWithFeedbackDTO(nome, userRole, feedback)
            );
        }
        return feedbacksWithUserDetails;
    }
}
