package service;

import client.PlaceClient;
import dto.PlaceDTO;
import dto.PlaceRequestDTO;
import model.Place;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.PlaceRepository;

import static org.springframework.http.HttpStatus.*;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceClient placeClient;

    public PlaceService(PlaceRepository placeRepository, PlaceClient placeClient) {
        this.placeRepository = placeRepository;
        this.placeClient = placeClient;
    }

    public Mono<ResponseEntity<String>> createPlace(PlaceDTO placeDTO, String token) {
        String action = "createPlace";

        return placeClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {

                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract())
                            .flatMap(existingPlace ->
                                    Mono.just(ResponseEntity.status(CONFLICT)
                                            .body("A bacia: " + placeDTO.getName() + " já existe para o contrato: " + placeDTO.getProjectContract()))
                            )
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        Place place = new Place();
                                        place.setName(placeDTO.getName());
                                        place.setProjectContract(placeDTO.getProjectContract());

                                        return placeRepository.save(place)
                                                .map(savedUser -> ResponseEntity.status(CREATED)
                                                        .body("Bacia criada com sucesso"));
                                    })
                            );
                });
    }


    public Mono<ResponseEntity<String>> deletePlace(PlaceDTO placeDTO, String token) {
        String action = "deletePlace";

        return placeClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == HttpStatus.FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    // Prossegue com a exclusão se a permissão for concedida
                    return placeRepository.findByNameAndProjectContract(placeDTO.getName(), placeDTO.getProjectContract())
                            .flatMap(existingPlace ->
                                    placeRepository.delete(existingPlace)
                                            .then(Mono.just(ResponseEntity.status(HttpStatus.OK)
                                                    .body("Bacia deletada com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NO_CONTENT)
                                    .body("Bacia não encontrada com o nome e contrato de projeto fornecidos")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão da bacia: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> existsByNameAndProjectContract(PlaceRequestDTO placeRequestDTO, String token) {

        String action = "existPlace";

        return placeClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == HttpStatus.NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == HttpStatus.FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return placeRepository.findByNameAndProjectContract(placeRequestDTO.getName(), placeRequestDTO.getContract())
                            .flatMap(existingPlace ->
                                    Mono.just(ResponseEntity.status(OK)
                                            .body("Bacia encontrada")))
                            .switchIfEmpty(
                                    Mono.just(ResponseEntity.status(NO_CONTENT)
                                            .body("Bacia não encontrada")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar a existência da bacia: " + error.getMessage())));
    }
}
