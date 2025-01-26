package controller;

import dto.PlaceDTO;
import dto.PlaceRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import service.PlaceService;

@RestController
@RequestMapping("api/places")
@Tag(name = "Place API", description = "APIs para gerenciamento de bacias (places)")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(
            summary = "Criação de Bacia",
            description = "Cria uma nova bacia com base nas informações fornecidas, se o usuário tiver permissão.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da bacia a ser criada.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bacia criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (token ou role inválidos)."),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para criar a bacia."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada."),
            @ApiResponse(responseCode = "409", description = "Bacia já existente para o contrato fornecido."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha no serviço de permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a criação.")
    })
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createPlace(
            @RequestBody PlaceDTO placeDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeService.createPlace(placeDTO, token);
    }

    @Operation(
            summary = "Exclusão de Bacia",
            description = "Exclui uma bacia existente com base nas informações fornecidas, se o usuário tiver permissão.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da bacia a ser deletada.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bacia deletada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Bacia não encontrada."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (token ou role inválidos)."),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para deletar a bacia."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha no serviço de permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a exclusão.")
    })
    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deletePlace(
            @RequestBody PlaceDTO placeDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeService.deletePlace(placeDTO, token);
    }

    @Operation(
            summary = "Verificação de Existência de Bacia",
            description = "Verifica se uma bacia existe no sistema com base no nome e contrato fornecidos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da bacia para verificação.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceRequestDTO.class))
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bacia encontrada."),
            @ApiResponse(responseCode = "204", description = "Bacia não encontrada."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (token ou role inválidos)."),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para verificar a bacia."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha no serviço de permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a verificação.")
    })
    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existByNameAndProject(
            @RequestBody PlaceRequestDTO placeRequestDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeService.existsByNameAndProjectContract(placeRequestDTO, token);
    }
}
