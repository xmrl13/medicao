package controller;

import dto.EmailDTO;
import dto.UserRequestDTO;
import dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createUser(@RequestBody UserRequestDTO userRequestDTO, @RequestHeader("Authorization") String token) {
        return userService.createUser(userRequestDTO, token);
    }

    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteByEmail(@Valid @RequestBody EmailDTO emailDTO, @RequestHeader("Authorization") String token) {
        return userService.deleteUser(emailDTO, token);
    }

    @PostMapping("/update")
    public Mono<ResponseEntity<?>> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }
}
