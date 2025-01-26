package client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@RestController
@RequestMapping("api/itens")
public class ItemClient {

    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(ItemClient.class);

    public ItemClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://user").build();

    }

    public Mono<ResponseEntity<String>> hasPermission(String token, String action) {
        return webClient.post()
                .uri("api/users/has-permission/{token}/{action}", token, action)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()

                .onStatus(HttpStatusCode::isError, ClientResponse::createException)
                .toEntity(String.class)

                .onErrorResume(WebClientRequestException.class, ex -> {

                    log.error("Erro ao chamar serviço de user", ex);
                    return Mono.just(
                            ResponseEntity
                                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                                    .body("Serviço de usuário indisponível. Tente novamente mais tarde.")
                    );
                })

                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Serviço de user retornou erro: {}", ex.getStatusCode(), ex);

                    return Mono.just(
                            ResponseEntity
                                    .status(ex.getStatusCode())
                                    .body(ex.getResponseBodyAsString())
                    );
                });
    }

}
