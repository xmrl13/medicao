package client;

import dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import repository.UserRepository;
import service.UserService;

@Component
@RestController
@RequestMapping("api/users")
public class UserClient {

    private final UserRepository userRepository;
    private final UserService userService;

    public UserClient(WebClient.Builder webClientBuilder, UserRepository userRepository, UserService userService) {
        WebClient webClient = webClientBuilder.baseUrl("lb://filter").build();
        this.userRepository = userRepository;
        this.userService = userService;
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

    @PostMapping("/has-permission/{token}/{action}")
    public Mono<ResponseEntity<String>> hasPermission(@PathVariable String token, @PathVariable String action) {
        return userService.hasPermission(token, action);
    }

}
