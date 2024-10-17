package service;

import client.UserClient;
import dto.EmailDTO;
import dto.UserRequestDTO;
import dto.UserResponseDTO;
import dto.UserUpdateDTO;
import enums.Role;
import exceptions.EmailAlreadyExistsException;
import exceptions.InvalidSecretPhraseException;
import exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.UserCustomRepository;
import repository.UserRepository;

import java.lang.reflect.Method;

@Service
public class UserService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private UserCustomRepository userCustomRepository;

    public Mono<ResponseEntity<?>> createUser(UserRequestDTO userRequestDTO, String token) {
        return hasPermission(token, "canCreateUser")
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body((Object) "Sem permissão"));
                    }

                    return userRepository.findByEmail(userRequestDTO.getEmail())
                            .flatMap(existingUser -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body((Object) "Usuário com esse e-mail já existe")))
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        User user = new User();
                                        user.setName(userRequestDTO.getName());
                                        user.setEmail(userRequestDTO.getEmail());
                                        user.setRole(userRequestDTO.getRole());
                                        user.setSecretPhrase(userRequestDTO.getSecretPhrase());

                                        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
                                        user.setPassword(encodedPassword);

                                        return userRepository.save(user)
                                                .map(savedUser -> ResponseEntity.status(HttpStatus.CREATED).<Object>build());
                                    })
                            );
                });
    }


    public Mono<Void> deleteByEmail(String email) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("Pessoa não encontrada com o e-mail: " + email)))
                .flatMap(user -> userRepository.deleteById(user.getId()))
                .then();
    }

    public Mono<ResponseEntity<UserResponseDTO>> readByEmail(EmailDTO emailDTO) {
        return userRepository.findByEmail(emailDTO.getEmail())
                .map(user -> ResponseEntity.ok(convertToUserResponseDTO(user)))
                .switchIfEmpty(Mono.defer(() -> Mono.just(
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .build()
                )));
    }


    private UserResponseDTO convertToUserResponseDTO(User user) {
        return new UserResponseDTO(user.getEmail(), user.getName(), user.getRole());
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


    public Mono<ResponseEntity<?>> updateUser(@Valid UserUpdateDTO userUpdateDTO) {
        return userRepository.findByEmail(userUpdateDTO.getOldEmail())
                .switchIfEmpty(Mono.error(new UserNotFoundException("Pessoa não encontrada com o e-mail: " + userUpdateDTO.getOldEmail())))
                .flatMap(user -> {
                    if (passwordEncoder.matches(userUpdateDTO.getSecretPhrase(), user.getSecretPhrase())) {
                        if (userUpdateDTO.getName() != null) {
                            user.setName(userUpdateDTO.getName());
                        }
                        if (userUpdateDTO.getNewEmail() != null) {
                            if (user.getEmail().equals(userUpdateDTO.getNewEmail())) {
                                return Mono.error(new EmailAlreadyExistsException("O novo e-mail deve ser diferente do e-mail já cadastrado."));
                            }
                            return userRepository.findByEmail(userUpdateDTO.getNewEmail())
                                    .flatMap(p -> Mono.error(new EmailAlreadyExistsException("Email já cadastrado")))
                                    .then(Mono.defer(() -> {
                                        user.setEmail(userUpdateDTO.getNewEmail());
                                        return saveUser(user);
                                    }));
                        }
                        if (userUpdateDTO.getNewPassword() != null) {
                            if (!passwordEncoder.matches(userUpdateDTO.getNewPassword(), user.getPassword())) {
                                String hashedPassword = passwordEncoder.encode(userUpdateDTO.getNewPassword());
                                user.setPassword(hashedPassword);
                            } else {
                                return Mono.error(new IllegalArgumentException("A nova senha deve ser diferente da atual"));
                            }
                        }
                        return saveUser(user);
                    } else {
                        return Mono.error(new InvalidSecretPhraseException("Frase secreta incorreta"));
                    }
                });
    }

    private Mono<ResponseEntity<?>> saveUser(User user) {
        return userRepository.save(user).thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).build());
    }
}
