package service;

import client.PlaceItemClient;
import dto.PlaceItemRequestDTO;
import model.PlaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.PlaceItemRepository;

import static org.springframework.http.HttpStatus.*;

@Service
public class PlaceItemService {

    private final PlaceItemRepository placeItemRepository;

    @Autowired
    private PlaceItemClient placeItemClient;

    public PlaceItemService(PlaceItemRepository placeItemRepository) {
        this.placeItemRepository = placeItemRepository;
    }

    public Mono<ResponseEntity<String>> createPlaceItem(PlaceItemRequestDTO placeItemRequestDTO, String token) {
        String action = "createPlaceItem";

        return placeItemClient.hasPermission(token, action)
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
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    System.out.println(placeItemRequestDTO.getItemName());
                    System.out.println(placeItemRequestDTO.getItemUnit());
                    System.out.println(placeItemRequestDTO.getPlaceName());
                    System.out.println(placeItemRequestDTO.getProjectContract());

                    return placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract())
                            .flatMap(placeExistsResponse -> {
                                System.out.println(placeExistsResponse.getStatusCode());
                                if (placeExistsResponse.getStatusCode() == NOT_FOUND) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Bacia não encontrada, nome: " + placeItemRequestDTO.getPlaceName() + " contrato: " + placeItemRequestDTO.getProjectContract()));
                                } else if (placeExistsResponse.getStatusCode() != OK) {
                                    return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                            .body("Erro ao verificar existência da bacia"));
                                }

                                return placeItemClient.itemExists(token, placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit())
                                        .flatMap(itemExistsResponse -> {
                                            if (itemExistsResponse.getStatusCode() == NOT_FOUND) {
                                                return Mono.just(ResponseEntity.status(NOT_FOUND)
                                                        .body("Item não encontrado, nome: " + placeItemRequestDTO.getItemName() + " unidade: " + placeItemRequestDTO.getItemUnit()));
                                            } else if (itemExistsResponse.getStatusCode() != OK) {
                                                return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                                        .body("Erro ao verificar existência do item"));
                                            }

                                            return placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndPlaceProjectContract
                                                            (placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit()
                                                                    , placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()
                                                            )
                                                    .flatMap(existingPlaceItem ->
                                                            Mono.just(ResponseEntity.status(CONFLICT)
                                                                    .body("Já existe"))
                                                    )
                                                    .switchIfEmpty(
                                                            Mono.defer(() -> {
                                                                PlaceItem placeItem = new PlaceItem();
                                                                placeItem.setItemName(placeItemRequestDTO.getItemName());
                                                                placeItem.setItemUnit(placeItemRequestDTO.getItemUnit());
                                                                placeItem.setPlaceName(placeItemRequestDTO.getPlaceName());
                                                                placeItem.setPlaceProjectContract(placeItemRequestDTO.getProjectContract());
                                                                placeItem.setPredictedValue(placeItemRequestDTO.getPredictedValue());
                                                                return placeItemRepository.save(placeItem)
                                                                        .map(savedPlaceItem -> ResponseEntity.status(CREATED)
                                                                                .body(""));
                                                            })
                                                    );
                                        });
                            });
                });
    }

    public Mono<ResponseEntity<String>> deletePlaceItem(PlaceItemRequestDTO placeItemRequestDTO, String token) {

        String action = "deletePlaceItem";

        return placeItemClient.hasPermission(token, action)
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
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndPlaceProjectContract
                                    (placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit()
                                            , placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract()
                                    )
                            .flatMap(existingPlaceItem ->
                                    placeItemRepository.delete(existingPlaceItem)
                                            .then(Mono.just(ResponseEntity.status(OK)
                                                    .body("Item Deletado com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NOT_FOUND)
                                    .body("Não encontrado")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão do item: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> existsByNameAndContract(PlaceItemRequestDTO placeItemRequestDTO, String token) {

        String action = "existPlaceItem";

        return placeItemClient.hasPermission(token, action)
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
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return placeItemClient.placeExists(token, placeItemRequestDTO.getPlaceName(), placeItemRequestDTO.getProjectContract())
                            .flatMap(placeExistsResponse -> {
                                if (placeExistsResponse.getStatusCode() == NOT_FOUND) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Bacia não encontrada, nome: " + placeItemRequestDTO.getPlaceName() +
                                                    " contrato: " + placeItemRequestDTO.getProjectContract()));
                                } else if (placeExistsResponse.getStatusCode() != OK) {
                                    return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                            .body("Erro ao verificar existência da bacia"));
                                }

                                return placeItemClient.itemExists(token, placeItemRequestDTO.getItemName(), placeItemRequestDTO.getItemUnit())
                                        .flatMap(itemExistsResponse -> {
                                            if (itemExistsResponse.getStatusCode() == NOT_FOUND) {
                                                return Mono.just(ResponseEntity.status(NOT_FOUND)
                                                        .body("Item não encontrado, nome: " + placeItemRequestDTO.getItemName() +
                                                                " unidade: " + placeItemRequestDTO.getItemUnit()));
                                            } else if (itemExistsResponse.getStatusCode() != OK) {
                                                return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                                        .body("Erro ao verificar existência do item"));
                                            }

                                            return placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndPlaceProjectContract(
                                                            placeItemRequestDTO.getItemName(),
                                                            placeItemRequestDTO.getItemUnit(),
                                                            placeItemRequestDTO.getPlaceName(),
                                                            placeItemRequestDTO.getProjectContract())
                                                    .flatMap(existingPlaceItem ->
                                                            Mono.just(ResponseEntity.status(OK)
                                                                    .body("Item encontrado")))
                                                    .switchIfEmpty(
                                                            Mono.just(ResponseEntity.status(NOT_FOUND)
                                                                    .body("Item não encontrado")));
                                        });
                            });
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar a existência do item: " + error.getMessage())));

    }
}
