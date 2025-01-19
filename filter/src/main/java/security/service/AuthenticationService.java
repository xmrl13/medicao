package security.service;

import DTO.UserLoginRequest;
import DTO.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import security.exceptions.InvalidPasswordException;
import security.exceptions.UserNotFoundException;
import security.jwt.JwtTokenProvider;

import java.security.Key;
import java.util.Base64;

@Slf4j
@Service
public class AuthenticationService {

    private Key key;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final WebClient webClient;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthenticationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user/api/users").build();
    }


    public Mono<String> authenticate(UserLoginRequest loginRequest) {
        return webClient.get()

                .uri("/getuserbyemail/" + loginRequest.getEmail())
                .header("Internal-Call", "true")
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        response -> response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new UserNotFoundException("Usuário não encontrado: " + errorBody))
                        )
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        response -> response.bodyToMono(String.class).flatMap(errorBody ->
                                Mono.error(new RuntimeException("Erro no servidor ao buscar o usuário: " + errorBody))
                        )
                )
                .bodyToMono(UserResponse.class)
                .flatMap(userResponse -> {
                    if (userResponse != null && userResponse.isValid()) {
                        if (passwordMatches(loginRequest.getPassword(), userResponse.getPassword())) {

                            String token = jwtTokenProvider.generateToken(userResponse.getEmail(), userResponse.getRole());
                            return Mono.just(token);
                        } else {
                            return Mono.error(new InvalidPasswordException("Senha inválida"));
                        }
                    } else {
                        return Mono.error(new UserNotFoundException("Usuário não encontrado ou inativo"));
                    }
                });
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    private boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
