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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
public class PlaceItemService {

    private final PlaceItemRepository placeItemRepository;
    private final PlaceItemClient placeItemClient;

    public PlaceItemService(PlaceItemRepository placeItemRepository, PlaceItemClient placeItemClient) {
        this.placeItemRepository = placeItemRepository;
        this.placeItemClient = placeItemClient;
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

                                            return placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract
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
                                                                placeItem.setProjectContract(placeItemRequestDTO.getProjectContract());
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

                    return placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract
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

                                            return placeItemRepository.findByItemNameAndItemUnitAndPlaceNameAndProjectContract(
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

    public Mono<ResponseEntity<?>> getAllPlaceItensByToken(String token) {

        String action = "getPlaceItem";

        return placeItemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();

                    if (status == HttpStatus.FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN).build());
                    } else if (status != HttpStatus.OK) {
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
                    }

                    return placeItemClient.getEmailByToken(token)
                            .flatMap(emailResponse -> {
                                HttpStatus emailStatus = (HttpStatus) emailResponse.getStatusCode();

                                if (emailStatus == FORBIDDEN) {
                                    return Mono.just(ResponseEntity.status(FORBIDDEN).build());
                                } else if (emailStatus != OK) {
                                    return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
                                }

                                String email = emailResponse.getBody();
                                if (email == null || email.isEmpty()) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND).body("Email not found for token."));
                                }

                                return placeItemClient.getProjectsContractsByEmail(email, token)
                                        .flatMapMany(projectResponse -> {
                                            List<String> contracts = Arrays.stream(
                                                            projectResponse.getBody()
                                                                    .replace("[", "")
                                                                    .replace("]", "")
                                                                    .split(",")
                                                    )
                                                    .map(s -> s.replace("\"", "").trim())
                                                    .collect(Collectors.toList());

                                            System.out.println("Contracts from response (corrigido): " + contracts);

                                            return placeItemRepository.findByProjectContractIn(contracts);

                                        })
                                        .collectList()
                                        .flatMap(items -> {
                                            if (items.isEmpty()) {
                                                return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
                                            }
                                            return Mono.just(ResponseEntity.ok(items));
                                        });

                            })
                            .onErrorResume(error -> {
                                error.printStackTrace();
                                return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
                            });
                });
    }

}
