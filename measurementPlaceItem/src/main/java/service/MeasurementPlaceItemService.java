package service;

import client.MeasurementPlaceItemClient;
import dto.MeasurementPlaceItemDTO;
import model.MeasurementPlaceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.MeasurementPlaceItemRepository;

import static org.springframework.http.HttpStatus.*;

@Service
public class MeasurementPlaceItemService {

    private final MeasurementPlaceItemRepository measurementPlaceItemRepository;

    public MeasurementPlaceItemService(MeasurementPlaceItemRepository measurementPlaceItemRepository) {
        this.measurementPlaceItemRepository = measurementPlaceItemRepository;
    }

    @Autowired
    private MeasurementPlaceItemClient measurementPlaceItemClient;

    public Mono<ResponseEntity<String>> createMeasurementPlaceItem(MeasurementPlaceItemDTO measurementPlaceItemDTO, String token) {

        String action = "createMeasurementPlaceItem";

        return measurementPlaceItemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }

                    return measurementPlaceItemClient.measurementExists(token, measurementPlaceItemDTO.getProjectContract(), measurementPlaceItemDTO.getYearMonth())
                            .flatMap(measurementExistsResponse -> {
                                if (measurementExistsResponse.getStatusCode() == NOT_FOUND) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Medição não encontrada para o contrato: " + measurementPlaceItemDTO.getProjectContract()));
                                } else if (measurementExistsResponse.getStatusCode() != OK) {
                                    return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                            .body("Erro ao verificar existência da medição"));
                                }

                                return measurementPlaceItemClient.placeItemExists(token, measurementPlaceItemDTO.getPlaceName(),
                                                measurementPlaceItemDTO.getProjectContract(), measurementPlaceItemDTO.getItemName(),
                                                measurementPlaceItemDTO.getItemUnit())
                                        .flatMap(placeItemExistsResponse -> {
                                            if (placeItemExistsResponse.getStatusCode() == NOT_FOUND) {
                                                return Mono.just(ResponseEntity.status(NOT_FOUND)
                                                        .body("Item ou lugar não encontrado: " + measurementPlaceItemDTO.getPlaceName() + ", " + measurementPlaceItemDTO.getItemName()));
                                            } else if (placeItemExistsResponse.getStatusCode() != OK) {
                                                return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                                        .body("Erro ao verificar existência do item e lugar"));
                                            }


                                            return measurementPlaceItemRepository.findByPlaceNameAndProjectContractAndItemNameAndItemUnitAndYearMonth(
                                                            measurementPlaceItemDTO.getPlaceName(),
                                                            measurementPlaceItemDTO.getProjectContract(),
                                                            measurementPlaceItemDTO.getItemName(),
                                                            measurementPlaceItemDTO.getItemUnit(),
                                                            measurementPlaceItemDTO.getYearMonth())
                                                    .flatMap(existingItem -> Mono.just(ResponseEntity.status(CONFLICT)
                                                            .body("A medição para o item já existe no lugar e período especificados")))
                                                    .switchIfEmpty(Mono.defer(() -> {
                                                        MeasurementPlaceItem newMeasurementPlaceItem = new MeasurementPlaceItem();
                                                        newMeasurementPlaceItem.setPlaceName(measurementPlaceItemDTO.getPlaceName());
                                                        newMeasurementPlaceItem.setItemName(measurementPlaceItemDTO.getItemName());
                                                        newMeasurementPlaceItem.setItemUnit(measurementPlaceItemDTO.getItemUnit());
                                                        newMeasurementPlaceItem.setYearMonth(measurementPlaceItemDTO.getYearMonth());
                                                        newMeasurementPlaceItem.setPredictedValue(measurementPlaceItemDTO.getPredictedValue());
                                                        newMeasurementPlaceItem.setAccumulatedValue(measurementPlaceItemDTO.getAccumulatedValue());
                                                        newMeasurementPlaceItem.setProjectContract(measurementPlaceItemDTO.getProjectContract());
                                                        newMeasurementPlaceItem.setStartDate(measurementPlaceItemDTO.getStartDate());
                                                        newMeasurementPlaceItem.setEndDate(measurementPlaceItemDTO.getEndDate());

                                                        return measurementPlaceItemRepository.save(newMeasurementPlaceItem)
                                                                .map(savedItem -> ResponseEntity.status(CREATED)
                                                                        .body("Medição criada com sucesso"));
                                                    }));
                                        });
                            });
                });
    }

    public Mono<ResponseEntity<String>> deleteMeasurement(MeasurementPlaceItemDTO measurementPlaceItemDTO, String token) {
        String action = "deleteMeasurementPlaceItem";

        return measurementPlaceItemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }

                    return measurementPlaceItemRepository.findByPlaceNameAndProjectContractAndItemNameAndItemUnitAndYearMonth(
                                    measurementPlaceItemDTO.getPlaceName(),
                                    measurementPlaceItemDTO.getProjectContract(),
                                    measurementPlaceItemDTO.getItemName(),
                                    measurementPlaceItemDTO.getItemUnit(),
                                    measurementPlaceItemDTO.getYearMonth())
                            .flatMap(existingItem -> measurementPlaceItemRepository.delete(existingItem)
                                    .then(Mono.just(ResponseEntity.status(OK)
                                            .body("Medição deletada com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NOT_FOUND)
                                    .body("Medição não encontrada para os parâmetros fornecidos")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar exclusão: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> existsByPlaceNameProjectContractItemNameItemUnitYearMonth(MeasurementPlaceItemDTO measurementPlaceItemDTO, String token) {

        String action = "existMeasurementPlaceItem";

        return measurementPlaceItemClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == SERVICE_UNAVAILABLE || status == INTERNAL_SERVER_ERROR) {
                        return Mono.just(ResponseEntity.status(FAILED_DEPENDENCY).body("Uma dependência falhou."));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(status).body(message));
                    }
                    return measurementPlaceItemRepository.findByPlaceNameAndProjectContractAndItemNameAndItemUnitAndYearMonth(
                                    measurementPlaceItemDTO.getPlaceName(),
                                    measurementPlaceItemDTO.getProjectContract(),
                                    measurementPlaceItemDTO.getItemName(),
                                    measurementPlaceItemDTO.getItemUnit(),
                                    measurementPlaceItemDTO.getYearMonth())
                            .flatMap(existingItem -> Mono.just(ResponseEntity.status(OK)
                                    .body("Medição encontrada para os parâmetros fornecidos")))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NOT_FOUND)
                                    .body("Medição não encontrada para os parâmetros fornecidos")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar existência: " + error.getMessage())));
    }
}