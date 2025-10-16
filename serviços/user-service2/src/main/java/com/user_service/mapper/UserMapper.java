package com.user_service.mapper;

import com.user_service.dto.UserDTO;
import com.user_service.dto.UserSignupDTO;
import com.user_service.entity.User;
import com.user_service.entity.UserRole;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public User mapUserSignupDTOtoUser(UserSignupDTO userSignupDTO, String encodedPassword) {
        User user = new User();
        user.setUserName(userSignupDTO.getUserName());
        user.setName(userSignupDTO.getName());
        user.setEmail(userSignupDTO.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(userSignupDTO.getUserRole());
        return user;
    }

    public UserDTO mapUserToUserDTO(User user){
        return new UserDTO
                (
                user.getId(),
                user.getName(),
                user.getUserName(),
                user.getEmail(),
                user.getRole()
        );
    }
    public List<UserDTO> mapListOfUserDTO(List<User> users){
        List<UserDTO> listUsersDTO = new ArrayList<>();
        for (User user : users ){
            listUsersDTO.add(mapUserToUserDTO(user));
        }
        return  listUsersDTO;
    }
}
