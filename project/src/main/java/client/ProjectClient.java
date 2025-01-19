package client;


import dto.ProjectDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import service.ProjectService;

@Component
@RestController
@RequestMapping("api/projects")
public class ProjectClient {

    private final WebClient webClient;
    private final ProjectService projectService;

    public ProjectClient(WebClient.Builder webClientBuilder, @Lazy ProjectService projectService) {
        this.webClient = webClientBuilder.baseUrl("lb://user").build();
        this.projectService = projectService;
    }


    public Mono<ResponseEntity<String>> hasPermission(String token, String action) {
        return webClient.post()
                .uri("api/users/has-permission/{token}/{action}", token, action)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ação não encontrada"))
                )
                .toEntity(String.class)
                .onErrorResume(ResponseStatusException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getReason()))
                );
    }
}
