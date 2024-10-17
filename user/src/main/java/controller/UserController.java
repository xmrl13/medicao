package controller;

import dto.EmailDTO;
import dto.UserRequestDTO;
import dto.UserResponseDTO;
import dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import service.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Mono<ResponseEntity<?>> createUser(@RequestBody UserRequestDTO userRequestDTO, @RequestHeader("Authorization") String token) {
        return userService.createUser(userRequestDTO, token);
    }

    @PostMapping("/deletebyemail")
    public Mono<ResponseEntity<?>> deleteByEmail(@Valid @RequestBody EmailDTO emailDTO) {
        return userService.deleteByEmail(emailDTO.getEmail())
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @PostMapping("/update")
    public Mono<ResponseEntity<?>> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }

    @GetMapping("/read")
    public Mono<ResponseEntity<UserResponseDTO>> find(@RequestParam("email") String email) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setEmail(email);
        return userService.readByEmail(emailDTO);
    }
}
