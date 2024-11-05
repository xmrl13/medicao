package service;

import dto.EmailDTO;
import dto.UserRequestDTO;
import dto.UserUpdateDTO;
import enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.UserRepository;

import java.lang.reflect.Method;
import java.security.Key;
import java.util.Base64;

@Service
public class UserService {

    private Key key;

    @Value("${jwt.secret}")
    private String secret;

    @PostConstruct
    public void init() {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(secretBytes);
    }

    private final UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ResponseEntity<String>> deleteUser(EmailDTO emailDTO, String token) {
        String action = "deleteUser";

        return hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == HttpStatus.FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Sem permissão para deletar usuário"));
                    } else if (status != HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return userRepository.findByEmail(emailDTO.getEmail())
                            .flatMap(existingUser ->
                                    userRepository.delete(existingUser)
                                            .then(Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                    .body("Usuário deletado com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("Usuário não encontrado: " + emailDTO.getEmail())));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão do usuário: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> createUser(UserRequestDTO userRequestDTO, String token) {
        String action = "createUser";

        return hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == HttpStatus.FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Sem permissão para criar usuário"));
                    } else if (status != HttpStatus.OK) {

                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return userRepository.findByEmail(userRequestDTO.getEmail())
                            .flatMap(existingUser ->
                                    Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                            .body("Usuário com esse e-mail já existe"))
                            )
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        User user = new User();
                                        user.setName(userRequestDTO.getName());
                                        user.setEmail(userRequestDTO.getEmail());
                                        user.setRole(userRequestDTO.getRole());

                                        String encodedSecretPhrase = passwordEncoder.encode(userRequestDTO.getSecretPhrase());
                                        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
                                        user.setPassword(encodedPassword);
                                        user.setSecretPhrase(encodedSecretPhrase);

                                        return userRepository.save(user)
                                                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED)
                                                        .body("Usuário criado com sucesso"));
                                    })
                            );
                });
    }

    public Mono<ResponseEntity<?>> updateUser(@Valid UserUpdateDTO userUpdateDTO) {

        return userRepository.findByEmail(userUpdateDTO.getOldEmail())
                .flatMap(user -> {
                    if (passwordEncoder.matches(userUpdateDTO.getSecretPhrase(), user.getSecretPhrase())) {
                        if (userUpdateDTO.getName() != null) {
                            user.setName(userUpdateDTO.getName());
                        }
                        if (userUpdateDTO.getNewEmail() != null) {
                            if (user.getEmail().equals(userUpdateDTO.getNewEmail())) {
                                return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                        .body("O novo e-mail deve ser diferente do e-mail já cadastrado."));
                            }
                            return userRepository.findByEmail(userUpdateDTO.getNewEmail())
                                    .flatMap(existingUser ->
                                            Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                                    .body("Email já cadastrado"))
                                    )
                                    .switchIfEmpty(Mono.defer(() -> {
                                        user.setEmail(userUpdateDTO.getNewEmail());
                                        return saveUser(user)
                                                .map(savedUser -> ResponseEntity.status(HttpStatus.OK).build());
                                    }));
                        }
                        if (userUpdateDTO.getNewPassword() != null) {
                            if (!passwordEncoder.matches(userUpdateDTO.getNewPassword(), user.getPassword())) {
                                String hashedPassword = passwordEncoder.encode(userUpdateDTO.getNewPassword());
                                user.setPassword(hashedPassword);
                            } else {
                                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body("A nova senha deve ser diferente da atual"));
                            }
                        }
                        return saveUser(user)
                                .map(savedUser -> ResponseEntity.status(HttpStatus.OK).build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Frase secreta incorreta"));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pessoa não encontrada com o e-mail: " + userUpdateDTO.getOldEmail())));
    }

    public Mono<ResponseEntity<String>> hasPermission(String token, String action) {
        if (token.startsWith("Bearer ")) {
            token = token.replace("Bearer ", "");
        }

        return getRoleFromToken(token)
                .flatMap(roleName -> {

                    Role role;
                    try {
                        role = Role.valueOf(roleName);
                    } catch (IllegalArgumentException e) {
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Role '" + roleName + "' não é válida"));
                    }

                    try {
                        Method method = Role.class.getMethod(action);
                        boolean hasPermission = (boolean) method.invoke(role);
                        if (hasPermission) {
                            return Mono.just(ResponseEntity.ok("Permissão concedida"));
                        } else {
                            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body("Sem permissão para a ação: " + action));
                        }
                    } catch (NoSuchMethodException e) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("A ação '" + action + "' não existe para a role " + roleName));
                    } catch (Exception e) {

                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + e.getMessage()));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Sem permissão: role não encontrada")));
    }


    public Mono<String> getRoleFromToken(String token) {
        return Mono.defer(() -> {
            try {
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)  // Usar a chave apropriada
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                String role = claims.get("role", String.class);
                return Mono.just(role);
            } catch (Exception e) {
                return Mono.error(e);
            }
        });
    }

    private Mono<ResponseEntity<?>> saveUser(User user) {
        return userRepository.save(user).thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).build());
    }
}
