package controller;

import dto.PlaceDTO;
import dto.PlaceRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.PlaceService;

@RestController
@RequestMapping("api/places")
@Tag(name = "Place Controller", description = "Controlador responsável pela criação, verificação e exclusão de bacias.")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @Operation(
            summary = "Cria uma bacia",
            description = "Cria uma bacia com base nas informações fornecidas no corpo da requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bacia criada com sucesso."),
            @ApiResponse(responseCode = "403", description = "Ação não autorizada. Verifique o token."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada. Verifique o token."),
            @ApiResponse(responseCode = "409", description = "A bacia já existe para o contrato informado."),
            @ApiResponse(responseCode = "424", description = "Erro interno associado a uma dependência.")
    })
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createPlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informações da bacia a ser criada.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlaceDTO.class),
                            mediaType = "application/json"
                    )
            )
            @RequestBody PlaceDTO placeDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeService.createPlace(placeDTO, token);
    }

    @Operation(
            summary = "Deleta uma bacia",
            description = "Exclui uma bacia com base nas informações fornecidas no corpo da requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bacia deletada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Bacia não encontrada."),
            @ApiResponse(responseCode = "403", description = "Ação não autorizada. Verifique o token."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada. Verifique o token."),
            @ApiResponse(responseCode = "424", description = "Erro interno associado a uma dependência."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deletePlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informações da bacia a ser deletada.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlaceDTO.class),
                            mediaType = "application/json"
                    )
            )
            @RequestBody PlaceDTO placeDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeService.deletePlace(placeDTO, token);
    }

    @Operation(
            summary = "Verifica se uma bacia existe",
            description = "Verifica se uma bacia existe no sistema com base no nome e contrato fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bacia encontrada."),
            @ApiResponse(responseCode = "204", description = "Bacia não encontrada."),
            @ApiResponse(responseCode = "403", description = "Ação não autorizada. Verifique o token."),
            @ApiResponse(responseCode = "424", description = "Erro interno associado a uma dependência."),
            @ApiResponse(responseCode = "500", description = "Erro interno.")
    })
    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existByNameAndProject(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para verificar a existência da bacia.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlaceRequestDTO.class),
                            mediaType = "application/json"
                    )
            )
            @RequestBody PlaceRequestDTO placeRequestDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeService.existsByNameAndProjectContract(placeRequestDTO, token);
    }
}
