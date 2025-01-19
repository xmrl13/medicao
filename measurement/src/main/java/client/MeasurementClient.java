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
import service.MeasurementService;

import java.util.HashMap;
import java.util.Map;

@Component
@RestController
@RequestMapping("api/measurements")
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
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ação não encontrada"))
                )
                .toEntity(String.class)
                .onErrorResume(ResponseStatusException.class, ex ->
                        Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getReason()))
                );
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
                        // Retorna diretamente a resposta 404 com mensagem
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Projeto não encontrado"));
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
