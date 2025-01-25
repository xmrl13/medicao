package service;

import client.MeasurementClient;
import dto.MeasurementDTO;
import dto.MeasurementRequestDTO;
import model.Measurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import repository.MeasurementRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private MeasurementClient measurementClient;

    @InjectMocks
    private MeasurementService measurementService;

    private MeasurementDTO measurementDTO;
    private String token;

    @BeforeEach
    void setup() {
        token = "token-teste";
        measurementDTO = new MeasurementDTO();
        measurementDTO.setProjectContract("CONTRATO-123");
        measurementDTO.setYearMonth("2025-01");
        measurementDTO.setStartDate(LocalDate.parse("2025-01-01"));
        measurementDTO.setEndDate(LocalDate.parse("2025-01-31"));
    }

    @Test
    void createMeasurement_AcaoNaoEncontrada() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Ação não encontrada")));

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Ação não encontrada: createMeasurement", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createMeasurement_PermissaoNegada() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.status(FORBIDDEN).body("Sem permissão")));

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FORBIDDEN, response.getStatusCode());
                    assertEquals("Sem permissão para realizar essa ação", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createMeasurement_ErroAoVerificarPermissao() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Erro genérico")));

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FAILED_DEPENDENCY, response.getStatusCode());
                    assertEquals("Erro ao verificar permissão: Erro genérico", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createMeasurement_ProjetoNaoEncontrado() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(measurementClient.projectExists(token, measurementDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Projeto não encontrado")));

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Projeto não encontrado para o contrato: CONTRATO-123", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createMeasurement_ErroAoVerificarProjeto() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(measurementClient.projectExists(token, measurementDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Erro no serviço")));

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FAILED_DEPENDENCY, response.getStatusCode());
                    assertEquals("Erro ao verificar existência do projeto", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createMeasurement_MedicaoJaExiste() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(measurementClient.projectExists(token, measurementDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.ok("Projeto encontrado")));

        Measurement existingMeasurement = new Measurement();
        existingMeasurement.setProjectContract("CONTRATO-123");
        existingMeasurement.setYearMonth("2025-01");

        when(measurementRepository.findByProjectContractAndYearMonth("CONTRATO-123", "2025-01"))
                .thenReturn(Mono.just(existingMeasurement));

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(CONFLICT, response.getStatusCode());
                    assertEquals("A medição: 2025-01 já existe para o contrato: CONTRATO-123", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createMeasurement_CriadoComSucesso() {
        when(measurementClient.hasPermission(token, "createMeasurement"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(measurementClient.projectExists(token, measurementDTO.getProjectContract()))
                .thenReturn(Mono.just(ResponseEntity.ok("Projeto encontrado")));

        when(measurementRepository.findByProjectContractAndYearMonth("CONTRATO-123", "2025-01"))
                .thenReturn(Mono.empty());

        when(measurementRepository.save(any(Measurement.class)))
                .thenAnswer(invocation -> {
                    Measurement measurementSalva = invocation.getArgument(0);
                    return Mono.just(measurementSalva);
                });

        Mono<ResponseEntity<String>> result = measurementService.createMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(CREATED, response.getStatusCode());
                    assertEquals("Medição criada com sucesso", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteMeasurement_AcaoNaoEncontrada() {
        String action = "deleteMeasurement";

        when(measurementClient.hasPermission(token, action))
                .thenReturn(Mono.just(ResponseEntity.status(NOT_FOUND).body("Ação não encontrada")));

        Mono<ResponseEntity<String>> result = measurementService.deleteMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Ação não encontrada: deleteMeasurement", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteMeasurement_SemPermissao() {
        String action = "deleteMeasurement";

        when(measurementClient.hasPermission(token, action))
                .thenReturn(Mono.just(ResponseEntity.status(FORBIDDEN).body("Sem permissão")));

        Mono<ResponseEntity<String>> result = measurementService.deleteMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FORBIDDEN, response.getStatusCode());
                    assertEquals("Sem permissão para realizar essa ação", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteMeasurement_ErroAoVerificarPermissao() {
        String action = "deleteMeasurement";

        when(measurementClient.hasPermission(token, action))
                .thenReturn(Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Erro")));

        Mono<ResponseEntity<String>> result = measurementService.deleteMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FAILED_DEPENDENCY, response.getStatusCode());
                    assertEquals("Erro ao verificar permissão: Erro", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteMeasurement_MedicaoNaoEncontrada() {
        String action = "deleteMeasurement";

        when(measurementClient.hasPermission(token, action))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(measurementRepository.findByProjectContractAndYearMonth("CONTRATO-123", "2025-01"))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = measurementService.deleteMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NO_CONTENT, response.getStatusCode());
                    assertEquals("Medição não encontrada com o nome e contrato fornecidos", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteMeasurement_Sucesso() {
        String action = "deleteMeasurement";

        when(measurementClient.hasPermission(token, action))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        Measurement existingMeasurement = new Measurement();
        existingMeasurement.setProjectContract("CONTRATO-123");
        existingMeasurement.setYearMonth("2025-01");

        when(measurementRepository.findByProjectContractAndYearMonth("CONTRATO-123", "2025-01"))
                .thenReturn(Mono.just(existingMeasurement));

        when(measurementRepository.delete(existingMeasurement))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = measurementService.deleteMeasurement(measurementDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Medição deletada com sucesso", response.getBody());
                })
                .verifyComplete();
    }
}
