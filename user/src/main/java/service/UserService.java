package service;

import client.UserClient;
import dto.EmailDTO;
import dto.UserRequestDTO;
import dto.UserUpdateDTO;
import enums.Role;
import jakarta.validation.Valid;
import model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import repository.UserRepository;

import java.lang.reflect.Method;

@Service
public class UserService {

    private final UserClient userClient;

    private final UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserClient userClient, UserRepository userRepository) {
        this.userClient = userClient;
        this.userRepository = userRepository;
    }

    public Mono<ResponseEntity<?>> createUser(UserRequestDTO userRequestDTO, String token) {

        return hasPermission(token, "canCreateUser")
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body((Object) "Sem permissão"));
                    }

                    return Mono.defer(() -> userRepository.findByEmail(userRequestDTO.getEmail())
                            .publishOn(Schedulers.boundedElastic())
                            .flatMap(existingUser -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body((Object) "Usuário com esse e-mail já existe")))
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
                                                .publishOn(Schedulers.boundedElastic()) // Movemos a operação de salvar no banco de dados para o boundedElastic
                                                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).build());
                                    })
                            )
                    );
                });
    }


    public Mono<ResponseEntity<?>> deleteByEmail(EmailDTO emailDTO, String token) {

        return hasPermission(token, "canDeleteUser")
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body((Object) "Sem permissão"));
                    }

                    return Mono.defer(() -> userRepository.findByEmail(emailDTO.getEmail())
                            .publishOn(Schedulers.boundedElastic()) // Usando boundedElastic para busca no banco
                            .flatMap(existingUser -> userRepository.delete(existingUser)
                                    .publishOn(Schedulers.boundedElastic()) // Usando boundedElastic para a exclusão
                                    .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body((Object) "Deletado com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado"))) // 404 Not Found
                    );
                });
    }

    public Mono<ResponseEntity<?>> updateUser(@Valid UserUpdateDTO userUpdateDTO) {

        return Mono.defer(() -> userRepository.findByEmail(userUpdateDTO.getOldEmail())
                .publishOn(Schedulers.boundedElastic()) // Mover busca no banco para boundedElastic
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
                                    .publishOn(Schedulers.boundedElastic()) // Mover nova busca no banco para boundedElastic
                                    .flatMap(existingUser -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT)
                                            .body("Email já cadastrado")))
                                    .switchIfEmpty(Mono.defer(() -> {
                                        user.setEmail(userUpdateDTO.getNewEmail());
                                        return saveUser(user)
                                                .publishOn(Schedulers.boundedElastic()) // Mover operação de salvar no banco
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
                                .publishOn(Schedulers.boundedElastic()) // Mover operação de salvar no banco
                                .map(savedUser -> ResponseEntity.status(HttpStatus.OK).build());
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Frase secreta incorreta"));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Pessoa não encontrada com o e-mail: " + userUpdateDTO.getOldEmail())))
        );
    }


    public Mono<Boolean> hasPermission(String token, String action) {
        return userClient.getUserRole(token)
                .flatMap(roleName -> {
                    try {
                        Role role = Role.valueOf(roleName);
                        Method method = Role.class.getMethod(action);
                        return Mono.just((boolean) method.invoke(role));
                    } catch (NoSuchMethodException e) {
                        return Mono.error(new IllegalArgumentException("A ação '" + action + "' não existe."));
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Erro ao verificar permissão para o usuário " + roleName + ": " + e.getMessage(), e));
                    }
                });
    }

    private Mono<ResponseEntity<?>> saveUser(User user) {
        return userRepository.save(user).thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).build());
    }
}
