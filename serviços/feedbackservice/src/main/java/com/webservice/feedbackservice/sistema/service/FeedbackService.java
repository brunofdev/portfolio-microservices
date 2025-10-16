package com.webservice.feedbackservice.sistema.service;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.UserDTO;
import com.webservice.feedbackservice.sistema.dto.UsersWithFeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.apiresponse.ApiResponse;
import com.webservice.feedbackservice.sistema.entities.Feedback;
import com.webservice.feedbackservice.sistema.exceptions.FeedbackNotFoundException;
import com.webservice.feedbackservice.sistema.exceptions.UnauthorizedCallException;
import com.webservice.feedbackservice.sistema.exceptions.UserDatailsNotFoundExcpetion;
import com.webservice.feedbackservice.sistema.mapper.FeedbackMapper;
import com.webservice.feedbackservice.sistema.messaging.producer.FeedbackEmailCreate;
import com.webservice.feedbackservice.sistema.repository.FeedbackRepository;
import com.webservice.feedbackservice.sistema.validation.FeedbackValidation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.List;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final ModelMapper modelMapper;
    private final FeedbackValidation feedbackValidation;
    private final WebClient.Builder webClientBuilder;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackEmailCreate feedbackEmailCreate;

    @Value("${userservice.api.url}")
    private String urlUserService;
    @Value("${api.internal.secret}")
    private String internalApiSecret;

    //este metodo utilitario serve para ser utilizado junto a biblioteca model mapper,
    //mepeando listas sem a necessidade de percorrer a lista individualmente
    private <S, T> List<T> mapListUtil(List<S> source, Class<T> targetClass){
        return source.stream()
                .map(element -> modelMapper.map(element, targetClass))
                .toList();
    }
    public FeedbackService(FeedbackRepository feedbackRepository, FeedbackValidation feedbackValidation,
                            ModelMapper modelMapper, WebClient.Builder webClientBuilder, FeedbackMapper feedbackMapper,
                            FeedbackEmailCreate feedbackEmailCreate){
        this.feedbackRepository = feedbackRepository;
        this.modelMapper = modelMapper;
        this.feedbackValidation = feedbackValidation;
        this.webClientBuilder = webClientBuilder;
        this.feedbackMapper = feedbackMapper;
        this.feedbackEmailCreate = feedbackEmailCreate;
    }

    public void createNewFeedback(FeedbackDTO dto){
        feedbackValidation.validateFeedback(dto);
        Feedback feedback = modelMapper.map(dto, Feedback.class);
        feedbackRepository.save(feedback);
        feedbackEmailCreate.sendToQueueRabbit(dto);
    }
    public  List<FeedbackDTO> listAllExists(){
        return mapListUtil(feedbackRepository.findAll(), FeedbackDTO.class);
    }
    private List<String> getUsersWithFeedback(){
        return feedbackRepository.findDistinctUserNames();
    }
    public List<UsersWithFeedbackDTO> listAllWithUserDetails() {
        List<String> userNames = feedbackRepository.findDistinctUserNames();
        List<Feedback> feedbacks = feedbackRepository.findAll();
        List<UserDTO> userDetails = requestUserDetailsForUserService(userNames);
        feedbackValidation.validateUserNames(userNames);
        return feedbackMapper.mapUsersWithFeedbackDTO(userDetails, feedbacks);
    }
    private List<UserDTO> requestUserDetailsForUserService(List<String> userNames) {
            WebClient webClient = webClientBuilder.baseUrl(urlUserService).build();
            ApiResponse<List<UserDTO>> apiResponse = webClient.post()
                    .uri("/internal/users/details") // Path corrigido que corresponde ao InternalUserController
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-Internal-Secret", internalApiSecret)
                    .body(Mono.just(userNames), new ParameterizedTypeReference<List<String>>() {}) // Usa a lista correta
                    .retrieve()
                    .onStatus(
                            status ->  status.is5xxServerError(),
                            response -> Mono.error(new UserDatailsNotFoundExcpetion("Dados dos usuarios indisponiveis no momento"))
                    ).onStatus(
                            status -> status.is4xxClientError(),
                               response -> Mono.error(new UnauthorizedCallException("Não fomos autorizados a acessar os dados"))
                    )

                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<UserDTO>>>() {})
                    .block();
            feedbackValidation.validateApiResponse(apiResponse);
            return apiResponse.getDados();
        }

    public void deleteFeedback(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException("o feedback com o id :::" + id + "::: não foi encontrado"));
        feedbackRepository.delete(feedback);
    }
    public void deleteFeedbacksWithUser(UserDTO userDTO){
        feedbackRepository.deleteUsersWithFeedback(userDTO.getUserName());
    }
}

