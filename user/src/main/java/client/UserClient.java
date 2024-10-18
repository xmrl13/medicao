package client;

import dto.UserResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import repository.UserRepository;

@Component
@RestController
@RequestMapping("api/users")
public class UserClient {

    private final UserRepository userRepository;

    private final WebClient webClient;

    public UserClient(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        this.webClient = webClientBuilder.baseUrl("lb://filter").build();
        this.userRepository = userRepository;
    }


    @GetMapping("getuserbyemail/{email}")
    public Mono<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        return userRepository.getUserByEmail(email)
                .map(user -> {
                    UserResponseDTO userDetails = new UserResponseDTO();
                    userDetails.setEmail(user.getEmail());
                    userDetails.setName(user.getName());
                    userDetails.setRole(user.getRole());
                    System.out.println(userDetails.getRole());
                    userDetails.setPassword(user.getPassword());
                    return userDetails;
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Usuário não encontrado")));
    }

    public Mono<String> getUserRole(String token) {
        System.out.println(token);
        return webClient.post()
                .uri("/api/auth/get-role")
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Erro ao buscar o role do usuário: " + e.getMessage(), e)));
    }
}
