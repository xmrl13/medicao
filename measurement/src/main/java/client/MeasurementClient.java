package client;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
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
import service.MeasurementService;

import java.util.HashMap;
import java.util.Map;

@Component
@RestController
@RequestMapping("api/measurements")
@Slf4j
public class MeasurementClient {

    private final WebClient webClient;
    private final MeasurementService measurementService;

    public MeasurementClient(WebClient.Builder webClientBuilder, @Lazy MeasurementService measurementService) {
        this.webClient = webClientBuilder.build();
        this.measurementService = measurementService;
    }

    public Mono<ResponseEntity<String>> hasPermission(String token, String action) {
        return webClient.post()
                .uri("lb://user/api/users/has-permission/{token}/{action}", token, action)
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

    public Mono<ResponseEntity<String>> projectExists(String token, String contract) {
        // Cria o objeto de request para enviar o contrato no body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("contract", contract);
        System.out.println(contract);

        return webClient.post()
                .uri("lb://project/api/projects/exist")
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(requestBody) // Define o corpo da requisição com o contrato
                .exchangeToMono(clientResponse -> {
                    HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();

                    System.out.println("Código de resposta HTTP recebido: " + statusCode);

                    if (statusCode == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Projeto não encontrado"));
                    }

                    if (statusCode.isError()) {
                        return clientResponse.bodyToMono(String.class)
                                .map(errorBody -> ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(errorBody));
                    }

                    return clientResponse.bodyToMono(String.class)
                            .map(body -> ResponseEntity.ok(body));
                })
                .onErrorResume(ex -> {
                    System.out.println("Erro inesperado: " + ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Erro ao acessar o serviço externo: " + ex.getMessage()));
                });

    }

}
