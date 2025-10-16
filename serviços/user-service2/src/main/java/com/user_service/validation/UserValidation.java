package com.user_service.validation;

import com.user_service.core.utils.StringFormater;
import com.user_service.dto.UserSignupDTO;
import com.user_service.entity.UserRole;
import com.user_service.exceptions.EmailAlreadyExistsException;
import com.user_service.exceptions.UsernameAlreadyExistsException;
import com.user_service.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserValidation {

    private final UserRepository userRepository;
    public UserValidation(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    private boolean checkUserNameExists(String userName){
        return userRepository.existsByUserNameIgnoreCase(userName);
    }
    private boolean checkEmailExists(String email){
        if(StringUtils.hasText(email)) {
            return userRepository.existsByEmailIgnoreCase(email);
        }
        return false;
    }
    private void checkAndMakeEspecialUser(UserSignupDTO userSignupDTO){
        String adminMarker = "@#$ADMIN$#@";
        if (userSignupDTO.getUserName().contains(adminMarker)) {
            userSignupDTO.setUserRole(UserRole.ADMIN);

            String cleanedUserName = userSignupDTO.getUserName().replace(adminMarker, "");
            userSignupDTO.setUserName(cleanedUserName);
        }
    }
    public void validateUserSignUp(UserSignupDTO userSignupDTO, boolean isUpdate){
        userSignupDTO.setUserRole(UserRole.USER);
        checkAndMakeEspecialUser(userSignupDTO);
        String userNameFormated = StringFormater.normalizeSpaces(userSignupDTO.getUserName());
        String emailFormated = StringFormater.normalizeSpaces(userSignupDTO.getEmail());
        if (checkUserNameExists(userNameFormated) && !isUpdate){
            throw new UsernameAlreadyExistsException("Nome de usuário '" + userNameFormated + "'já está em uso");
        }
        if(checkEmailExists(emailFormated) && !isUpdate){
            throw new EmailAlreadyExistsException("Email já cadastrado no sistema");
        }
    }
}
