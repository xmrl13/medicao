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
import service.MeasurementPlaceItemService;

import java.util.HashMap;
import java.util.Map;

@Component
@RestController
@RequestMapping("api/measurements")
public class MeasurementPlaceItemClient {

    private final WebClient webClient;
    private final MeasurementPlaceItemService measurementPlaceItemService;

    public MeasurementPlaceItemClient(WebClient.Builder webClientBuilder, @Lazy MeasurementPlaceItemService measurementPlaceItemService) {
        this.webClient = webClientBuilder.build();
        this.measurementPlaceItemService = measurementPlaceItemService;
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


    public Mono<ResponseEntity<String>> measurementExists(String token, String contract, String yearMonth) {

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("projectContract", contract);
        requestBody.put("yearMonth", yearMonth);

        return webClient.post()
                .uri("lb://measurement/api/measurements/exist") // Remove os parâmetros da URI
                .header(HttpHeaders.AUTHORIZATION, token) // Passa o token no cabeçalho
                .bodyValue(requestBody) // Define o corpo da requisição com o mapa
                .exchangeToMono(clientResponse -> {
                    HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();

                    System.out.println("Código de resposta HTTP recebido: " + statusCode);

                    if (statusCode == HttpStatus.NOT_FOUND) {
                        // Retorna diretamente a resposta 404 com mensagem
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Medição não encontrada"));
                    }

                    if (statusCode.isError()) {
                        // Tratamento genérico para outros códigos de erro
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ResponseStatusException(statusCode, errorBody)));
                    }

                    return clientResponse.toEntity(String.class);
                })
                .onErrorResume(ResponseStatusException.class, ex -> {
                    // Mantém o erro original caso seja 404
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(ex);
                    }
                    // Transforma outros erros em respostas customizadas
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getReason()));
                });
    }


    public Mono<ResponseEntity<String>> placeItemExists(String token, String placeName, String contract, String itemName, String itemUnit) {
        // Criando um DTO para o corpo da requisição
        Map<String, String> requestBody = Map.of(
                "placeName", placeName,
                "placeProjectContract", contract,
                "itemName", itemName,
                "itemUnit", itemUnit
        );

        return webClient.post()
                .uri("lb://placeItem/api/placesitens/exist") // URI sem parâmetros de caminho
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(requestBody) // Adicionando o corpo da requisição
                .exchangeToMono(clientResponse -> {
                    HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();

                    System.out.println("Código de resposta HTTP recebido: " + statusCode);

                    if (statusCode == HttpStatus.NOT_FOUND) {
                        // Retorna diretamente a resposta 404 com mensagem
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Lugar ou item não encontrado"));
                    }

                    if (statusCode.isError()) {
                        // Tratamento genérico para outros códigos de erro
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new ResponseStatusException(statusCode, errorBody)));
                    }

                    return clientResponse.toEntity(String.class);
                })
                .onErrorResume(ResponseStatusException.class, ex -> {
                    // Mantém o erro original caso seja 404
                    if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.error(ex);
                    }
                    // Transforma outros erros em respostas customizadas
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getReason()));
                });
    }

}
