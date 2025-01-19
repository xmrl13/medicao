package service;

import client.MeasurementClient;
import dto.MeasurementDTO;
import dto.MeasurementRequestDTO;
import model.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.MeasurementRepository;

import static org.springframework.http.HttpStatus.*;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Autowired
    private MeasurementClient measurementClient;

    public Mono<ResponseEntity<String>> createMeasurement(MeasurementDTO measurementDTO, String token) {
        String action = "createMeasurement";

        return measurementClient.hasPermission(token, action)
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

                    return measurementClient.projectExists(token, measurementDTO.getProjectContract())
                            .flatMap(projectExistsResponse -> {
                                if (projectExistsResponse.getStatusCode() == NOT_FOUND) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Projeto não encontrado para o contrato: " + measurementDTO.getProjectContract()));
                                } else if (projectExistsResponse.getStatusCode() != OK) {
                                    return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                            .body("Erro ao verificar existência do projeto"));
                                }

                                return measurementRepository.findByProjectContractAndYearMonth(measurementDTO.getProjectContract(), measurementDTO.getYearMonth())
                                        .flatMap(existingMeasurement ->
                                                Mono.just(ResponseEntity.status(CONFLICT)
                                                        .body("A medição: " + measurementDTO.getYearMonth() + " já existe para o contrato: " + measurementDTO.getProjectContract()))
                                        )
                                        .switchIfEmpty(
                                                Mono.defer(() -> {
                                                    Measurement measurement = new Measurement();
                                                    measurement.setProjectContract(measurementDTO.getProjectContract());
                                                    measurement.setYearMonth(measurementDTO.getYearMonth());
                                                    measurement.setStartDate(measurementDTO.getStartDate());
                                                    measurement.setEndDate(measurementDTO.getEndDate());
                                                    return measurementRepository.save(measurement)
                                                            .map(savedMeasurement -> ResponseEntity.status(CREATED)
                                                                    .body("Medição criada com sucesso"));
                                                })
                                        );
                            });
                });
    }


    public Mono<ResponseEntity<String>> deleteMeasurement(MeasurementDTO measurementDTO, String token) {
        String action = "deleteMeasurement";

        return measurementClient.hasPermission(token, action)
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

                    return measurementRepository.findByProjectContractAndYearMonth(measurementDTO.getProjectContract(), measurementDTO.getYearMonth())
                            .flatMap(existingMeasurement ->
                                    measurementRepository.delete((Measurement) existingMeasurement)
                                            .then(Mono.just(ResponseEntity.status(OK)
                                                    .body("Medição deletada com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NOT_FOUND)
                                    .body("Medição não encontrada com o nome e contrato fornecidos")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão da medição: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> existsByNameAndContract(MeasurementRequestDTO measurementRequestDTO, String token) {

        String action = "existMeasurement";

        return measurementClient.hasPermission(token, action)
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

                    // Verifica se o projeto existe
                    return measurementClient.projectExists(token, measurementRequestDTO.getProjectContract())
                            .flatMap(projectExistsResponse -> {
                                if (projectExistsResponse.getStatusCode() == NOT_FOUND) {
                                    return Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Projeto não encontrado para o contrato: " + measurementRequestDTO.getProjectContract()));
                                } else if (projectExistsResponse.getStatusCode() != OK) {
                                    return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                            .body("Erro ao verificar existência do projeto"));
                                }

                                // Verifica se a medição já existe no banco local
                                return measurementRepository.findByProjectContractAndYearMonth(measurementRequestDTO.getProjectContract(), measurementRequestDTO.getYearMonth())
                                        .flatMap(existingMeasurement ->
                                                Mono.just(ResponseEntity.status(OK)
                                                        .body("Medição encontrada")))
                                        .switchIfEmpty(
                                                Mono.just(ResponseEntity.status(NOT_FOUND)
                                                        .body("Medição não encontrada")));
                            });
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar a existência da medição: " + error.getMessage())));
    }
}
