package controller;

import dto.MeasurementDTO;
import dto.MeasurementRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.MeasurementService;

@RestController
@RequestMapping("api/measurements")
@Tag(name = "Measurement API", description = "APIs para gerenciamento de medições")
public class MeasurementController {

    private final MeasurementService measurementService;

    public MeasurementController(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @Operation(
            summary = "Criar Medição",
            description = "Cria uma nova medição se o usuário tiver permissão e o contrato existir.",
            requestBody = @RequestBody(
                    description = "Dados da medição a ser criada",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MeasurementDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Medição criada com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (token ou role inválidos)."),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para criar a medição."),
                    @ApiResponse(responseCode = "404", description = "Contrato ou ação não encontrada."),
                    @ApiResponse(responseCode = "409", description = "Medição já existe para o contrato."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência (ex.: serviço de contratos)."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao processar a criação.")
            }
    )
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createMeasurement(
            @RequestBody MeasurementDTO measurementDTO,
            @RequestHeader("Authorization") String token) {
        return measurementService.createMeasurement(measurementDTO, token);
    }

    @Operation(
            summary = "Deletar Medição",
            description = "Deleta uma medição existente se o usuário tiver permissão.",
            requestBody = @RequestBody(
                    description = "Dados da medição a ser deletada.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MeasurementDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medição deletada com sucesso."),
                    @ApiResponse(responseCode = "204", description = "Medição não encontrada."),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (token ou role inválidos)."),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para deletar a medição."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência (ex.: serviço de contratos)."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao processar a exclusão.")
            }
    )
    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteMeasurement(
            @RequestBody MeasurementDTO measurementDTO,
            @RequestHeader("Authorization") String token) {
        return measurementService.deleteMeasurement(measurementDTO, token);
    }

    @Operation(
            summary = "Verificar Existência de Medição",
            description = "Verifica se uma medição existe com base no contrato e no ano/mês fornecidos.",
            requestBody = @RequestBody(
                    description = "Dados da medição para verificação.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = MeasurementRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Medição encontrada."),
                    @ApiResponse(responseCode = "204", description = "Medição não encontrada."),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida (token ou role inválidos)."),
                    @ApiResponse(responseCode = "403", description = "Usuário sem permissão para verificar a medição."),
                    @ApiResponse(responseCode = "404", description = "Contrato ou ação não encontrada."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência (ex.: serviço de contratos)."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao processar a verificação.")
            }
    )
    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existMeasurement(
            @RequestBody MeasurementRequestDTO measurementRequestDTO,
            @RequestHeader("Authorization") String token) {
        return measurementService.existsByNameAndContract(measurementRequestDTO, token);
    }
}
