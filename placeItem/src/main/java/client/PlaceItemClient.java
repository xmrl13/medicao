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
import service.PlaceItemService;

import java.util.Map;

@Component
@RestController
@RequestMapping("api/projects")
public class PlaceItemClient {

    private final WebClient webClient;
    private final PlaceItemService placeItemService;

    public PlaceItemClient(WebClient.Builder webClientBuilder, @Lazy PlaceItemService placeItemService) {
        this.webClient = webClientBuilder.build();
        this.placeItemService = placeItemService;
    }


    public Mono<ResponseEntity<String>> hasPermission(String token, String action) {
        return webClient.post()
                .uri("lb://user/api/users/has-permission/{token}/{action}", token, action)
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

    public Mono<ResponseEntity<String>> itemExists(String token, String name, String unit) {
        // Cria um DTO ou Map para representar o corpo da requisição
        Map<String, String> requestBody = Map.of(
                "name", name,
                "unit", unit
        );

        return webClient.post()
                .uri("lb://item/api/itens/exist")
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(requestBody) // Adiciona o corpo da requisição
                .exchangeToMono(clientResponse -> {
                    HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();

                    System.out.println("Código de resposta HTTP recebido: " + statusCode);

                    if (statusCode == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Item não encontrado"));
                    }

                    if (statusCode.isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ResponseStatusException(statusCode, errorBody)));
                    }

                    return clientResponse.toEntity(String.class);
                })
                .onErrorResume(ResponseStatusException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(ex);
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getReason()));
                });
    }

    public Mono<ResponseEntity<String>> placeExists(String token, String name, String contract) {
        // Cria um DTO ou Map para representar o corpo da requisição
        Map<String, String> requestBody = Map.of(
                "name", name,
                "contract", contract
        );

        return webClient.post()
                .uri("lb://place/api/places/exist")
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(requestBody) // Adiciona o corpo da requisição
                .exchangeToMono(clientResponse -> {
                    HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();

                    System.out.println("Código de resposta HTTP recebido: " + statusCode);

                    if (statusCode == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Lugar não encontrado"));
                    }

                    if (statusCode.isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ResponseStatusException(statusCode, errorBody)));
                    }

                    return clientResponse.toEntity(String.class);
                })
                .onErrorResume(ResponseStatusException.class, ex -> {
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(ex);
                    }
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getReason()));
                });
    }

    public Mono<ResponseEntity<String>> getEmailByToken(String token) {
        return webClient.get()
                .uri("lb://user/api/users/get-email-by-token")
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

    public Mono<ResponseEntity<String>> getProjectsContractsByEmail(String email, String token) {
        return webClient.get()
                .uri("lb://project/api/projects/get-contracts-by-email?email={email}", email)
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
