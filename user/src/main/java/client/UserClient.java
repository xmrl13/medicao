package client;

import dto.UserResponseDTO;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import repository.UserCustomRepository;
import repository.UserRepository;

@Component
@RestController
@RequestMapping("api/users")
public class UserClient {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;
    @Autowired
    private UserCustomRepository userCustomRepository;

    @GetMapping("getuserbyemail/{email}")
    public Mono<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userRepository.getUserByEmail(email)
                .map(user -> {
                    UserResponseDTO userDetails = new UserResponseDTO();
                    userDetails.setEmail(user.getEmail());
                    userDetails.setName(user.getName());
                    userDetails.setRole(user.getRole());
                    userDetails.setPassword(user.getPassword());
                    return userDetails;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Usuário não encontrado")));
    }

    public Mono<String> getUserRole(String token) {
        String url = "http://auth-service/api/auth/get-role"; // URL do serviço registrado no Eureka

        return webClientBuilder.build()
                .method(HttpMethod.POST)
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Erro ao buscar o role do usuário: " + e.getMessage(), e)));
    }
}
