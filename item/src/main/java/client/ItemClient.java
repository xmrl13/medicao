package client;


import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import service.ItemService;

@Component
@RestController
@RequestMapping("api/itens")
public class ItemClient {

    private final WebClient webClient;
    private final ItemService itemService;

    public ItemClient(WebClient.Builder webClientBuilder, @Lazy ItemService itemService) {
        this.webClient = webClientBuilder.baseUrl("lb://user").build();
        this.itemService = itemService;
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
