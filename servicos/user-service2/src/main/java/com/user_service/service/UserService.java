package com.user_service.service;

import com.user_service.dto.FeedbackDTO;
import com.user_service.dto.UserCredentialDTO;
import com.user_service.dto.UserDTO;
import com.user_service.dto.UserSignupDTO;
import com.user_service.entity.UserRole;
import com.user_service.entity.User;
import com.user_service.exceptions.InvalidCredentialsException;
import com.user_service.exceptions.UserDontFoundException;
import com.user_service.exceptions.UserDontHaveEmailRegistered;
import com.user_service.exceptions.UserNotFoundException;
import com.user_service.mapper.UserMapper;
import com.user_service.messaging.producer.UserEmailProducer;
import com.user_service.messaging.producer.UserFeedbackProducer;
import com.user_service.repository.UserRepository;
import com.user_service.validation.UserValidation;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserEmailProducer userEmailProducer;
    private final UserFeedbackProducer userFeedbackProducer;

    public UserService(UserRepository userRepository, UserValidation userValidation, UserMapper userMapper, PasswordEncoder passwordEncoder, UserEmailProducer userRabbitProducer, UserFeedbackProducer userFeedbackProducer){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidation = userValidation;
        this.passwordEncoder = passwordEncoder;
        this.userEmailProducer = userRabbitProducer;
        this.userFeedbackProducer = userFeedbackProducer;
    }
    private User findUserByUserName(String userName){
        return userRepository.findByUserNameIgnoreCase(userName).
                orElseThrow(() -> new UserNotFoundException("Nome de usuário não encontrado"));
    }
    public UserDTO findUserDtoByUserName(String userName){
        User user = userRepository.findByUserNameIgnoreCase(userName).
                orElseThrow(() -> new UserNotFoundException("Nome de usuário não encontrado"));
        return userMapper.mapUserToUserDTO(user);
    }

    private UserDTO userHaveEmails(String userName){
        User user = userRepository.findByUserNameIgnoreCase(userName).orElseThrow(() -> new UserDontFoundException("Usuario não encontrado por userName"));
        if (user.getEmail().isEmpty()){
            throw new UserDontHaveEmailRegistered("O usuario não possui um email cadastrado");
        }
        return userMapper.mapUserToUserDTO(user);
    }
    public void sendEmailIfPostedFeedback(FeedbackDTO feedbackDTO){
        UserDTO userDTO = userHaveEmails(feedbackDTO.getUserName());
        userEmailProducer.sendToQueueNotifyPostedFeedback(userDTO);
    }
    private void sendWelcomeEmailIfApplicable(UserDTO userDTO){
        if(StringUtils.hasText(userDTO.getEmail())) {
            userEmailProducer.sendToQueueEmailWelcome(userDTO);
        }
    }
    public List<UserDTO> listAllUsers(){
        return userMapper.mapListOfUserDTO(userRepository.findAll());
}
    private String encodeUserPassword(String password){
        return passwordEncoder.encode(password);
    }
    public UserDTO createNewUser (UserSignupDTO userSignupDTO){
        userValidation.validateUserSignUp(userSignupDTO, false);
        User user = userMapper.mapUserSignupDTOtoUser(userSignupDTO, encodeUserPassword(userSignupDTO.getPassword()));
        userRepository.save(user);
        UserDTO userDTO = userMapper.mapUserToUserDTO(user);
        sendWelcomeEmailIfApplicable(userDTO);
        return userDTO;
    }
    public UserDTO authenticate(@Valid UserCredentialDTO userCredentialDTO) {
        User user = findUserByUserName(userCredentialDTO.getUserName());
        if(!passwordEncoder.matches(userCredentialDTO.getPassword(), user.getPassword()))
            throw new InvalidCredentialsException("Credenciais inválidas");
        return userMapper.mapUserToUserDTO(user);
    }
    public List<UserDTO> getUsersByUsernames(List<String> usernames){
       List<UserDTO> foundUsers = userMapper.mapListOfUserDTO(userRepository.findByUserNameIn(usernames));
       if(foundUsers.isEmpty()){
           throw new UserNotFoundException("Nenhum usuario foi encontrado");
       }
       return foundUsers;
    }
    public void deleteUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserDontFoundException("Usuario não encontrado no sistema"));
        userRepository.delete(user);
    }
    public void deleteUserByUserName(String userName){
        User user = userRepository.findByUserNameIgnoreCase(userName)
                .orElseThrow(() ->  new UserNotFoundException("Usuário :::" + userName + ":::não encontrado"));
        userRepository.delete(user);
        userFeedbackProducer.sendToQueueFeedbackCleanup(userMapper.mapUserToUserDTO(user));
    }
}
