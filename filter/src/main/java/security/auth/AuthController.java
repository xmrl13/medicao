package security.auth;

import DTO.UserLoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import security.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Mono<String>> login(@RequestBody UserLoginRequest request) {

        Mono<String> userResponse = authenticationService.authenticate(request);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/get-role")
    public String getRole(@RequestHeader("Authorization") String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authenticationService.getRoleFromToken(token);
    }
}
