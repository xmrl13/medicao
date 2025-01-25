package controller;

import dto.MeasurementDTO;
import dto.MeasurementRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import service.MeasurementService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MeasurementControllerTest {

    private MeasurementService measurementService;
    private MeasurementController measurementController;

    @BeforeEach
    void setUp() {
        measurementService = Mockito.mock(MeasurementService.class);
        measurementController = new MeasurementController(measurementService);
    }

    @Test
    @DisplayName("Deve criar uma medição com sucesso")
    void createMeasurement_Success() {

        MeasurementDTO measurementDTO = new MeasurementDTO();
        String token = "Bearer valid-token";

        when(measurementService.createMeasurement(eq(measurementDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Medição criada com sucesso")));

        Mono<ResponseEntity<String>> response = measurementController.createMeasurement(measurementDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals("Medição criada com sucesso", result.getBody());
        });

        verify(measurementService, times(1)).createMeasurement(eq(measurementDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar uma medição sem permissão")
    void createMeasurement_Forbidden() {

        MeasurementDTO measurementDTO = new MeasurementDTO();
        String token = "Bearer invalid-token";

        when(measurementService.createMeasurement(eq(measurementDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para realizar essa ação")));

        Mono<ResponseEntity<String>> response = measurementController.createMeasurement(measurementDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
            assertEquals("Sem permissão para realizar essa ação", result.getBody());
        });

        verify(measurementService, times(1)).createMeasurement(eq(measurementDTO), eq(token));
    }

    @Test
    @DisplayName("Deve deletar uma medição com sucesso")
    void deleteMeasurement_Success() {

        MeasurementDTO measurementDTO = new MeasurementDTO();
        String token = "Bearer valid-token";

        when(measurementService.deleteMeasurement(eq(measurementDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("Medição deletada com sucesso")));

        Mono<ResponseEntity<String>> response = measurementController.deleteMeasurement(measurementDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Medição deletada com sucesso", result.getBody());
        });

        verify(measurementService, times(1)).deleteMeasurement(eq(measurementDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao deletar uma medição inexistente")
    void deleteMeasurement_NotFound() {

        MeasurementDTO measurementDTO = new MeasurementDTO();
        String token = "Bearer valid-token";

        when(measurementService.deleteMeasurement(eq(measurementDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Medição não encontrada com o nome e contrato fornecidos")));

        Mono<ResponseEntity<String>> response = measurementController.deleteMeasurement(measurementDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
            assertEquals("Medição não encontrada com o nome e contrato fornecidos", result.getBody());
        });

        verify(measurementService, times(1)).deleteMeasurement(eq(measurementDTO), eq(token));
    }

    @Test
    @DisplayName("Deve verificar se uma medição existe")
    void existMeasurement_Success() {

        MeasurementRequestDTO measurementRequestDTO = new MeasurementRequestDTO();
        String token = "Bearer valid-token";

        when(measurementService.existsByNameAndContract(eq(measurementRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("Medição encontrada")));

        Mono<ResponseEntity<String>> response = measurementController.existMeasurement(measurementRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Medição encontrada", result.getBody());
        });

        verify(measurementService, times(1)).existsByNameAndContract(eq(measurementRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao verificar uma medição inexistente")
    void existMeasurement_NotFound() {

        MeasurementRequestDTO measurementRequestDTO = new MeasurementRequestDTO();
        String token = "Bearer valid-token";

        when(measurementService.existsByNameAndContract(eq(measurementRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).body("Medição não encontrada")));

        Mono<ResponseEntity<String>> response = measurementController.existMeasurement(measurementRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
            assertEquals("Medição não encontrada", result.getBody());
        });

        verify(measurementService, times(1)).existsByNameAndContract(eq(measurementRequestDTO), eq(token));
    }
}
