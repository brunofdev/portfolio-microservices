package com.webservice.feedbackservice.sistema.mapper;

import com.webservice.feedbackservice.sistema.dto.FeedbackDTO;
import com.webservice.feedbackservice.sistema.dto.UserDTO;
import com.webservice.feedbackservice.sistema.dto.UsersWithFeedbackDTO;
import com.webservice.feedbackservice.sistema.entities.Feedback;
import com.webservice.feedbackservice.sistema.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classe responsável por realizar o mapeamento entre entidades de Feedback
 * e os DTOs utilizados na camada de apresentação.
 *
 * <p>Essa classe centraliza conversões necessárias entre objetos da aplicação,
 * garantindo que informações adicionais como nome do usuário e papel (role)
 * sejam combinadas com o feedback antes de serem retornadas ao cliente.</p>
 */
@Component
public class FeedbackMapper {

    /**
     * Converte uma lista de {@link UserDTO} para um {@link Map}, onde a chave é o username
     * em letras maiúsculas e o valor é o próprio DTO.
     *
     * <p>Esse formato facilita buscas por usuários com base no nome de usuário,
     * independente de variação de caixa.</p>
     *
     * @param users Lista de usuários a serem convertidos.
     * @return Mapa com chave sendo o username em maiúsculo e valor sendo o DTO do usuário.
     */
    public Map<String, UserDTO> mapListUserDTOtoMap(List<UserDTO> users) {
        return users.stream().collect(Collectors.toMap(
                user -> user.getUserName().toUpperCase(Locale.ROOT),
                user -> user
        ));
    }

    /**
     * Cria um objeto {@link UsersWithFeedbackDTO} combinando um {@link Feedback}
     * com informações complementares sobre o usuário.
     *
     * @param nome Nome completo do usuário.
     * @param userRole Papel (role) do usuário na aplicação.
     * @param feedback Entidade de feedback original.
     * @return DTO contendo os dados combinados para retorno.
     */
    public UsersWithFeedbackDTO mapUsersWithFeedbackDTO(String nome, UserRole userRole, Feedback feedback) {
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
    /**
     * Converte uma lista de {@link Feedback} associando informações de usuário
     * a cada feedback. Caso um usuário não seja encontrado, valores padrão
     * são utilizados ("Nome não encontrado" e {@link UserRole#USER}).
     *
     * @param usersDetails Lista de detalhes dos usuários.
     * @param feedbacks Lista de feedbacks registrados.
     * @return Lista de {@link UsersWithFeedbackDTO} contendo dados combinados.
     */
    public List<UsersWithFeedbackDTO> mapUsersWithFeedbackDTO(List<UserDTO> usersDetails, List<Feedback> feedbacks) {
        Map<String, UserDTO> userMapByUsername = mapListUserDTOtoMap(usersDetails);
        return feedbacks.stream()
                .map(feedback -> {
                    String feedbackUserNameUpperCase = feedback.getUserName().toUpperCase(Locale.ROOT);
                    UserDTO userFound = userMapByUsername.get(feedbackUserNameUpperCase);

                    String nome = "Nome não encontrado";
                    UserRole userRole = UserRole.USER;

                    if (userFound != null) {
                        nome = userFound.getNome();
                        userRole = userFound.getUserRole() != null ? userFound.getUserRole() : UserRole.USER;
                    }
                    return mapUsersWithFeedbackDTO(nome, userRole, feedback);
                })
                .collect(Collectors.toList());
    }
}
