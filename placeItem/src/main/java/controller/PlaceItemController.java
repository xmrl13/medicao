package controller;

import dto.PlaceItemRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.PlaceItemService;

@RestController
@RequestMapping("api/placesitens")
@Tag(name = "Place Item API", description = "APIs para gerenciamento de PlaceItens")
public class PlaceItemController {

    private final PlaceItemService placeItemService;

    public PlaceItemController(PlaceItemService placeItemService) {
        this.placeItemService = placeItemService;
    }

    @Operation(
            summary = "Criação de PlaceItem",
            description = "Cria um novo PlaceItem com base nos dados fornecidos, se o usuário tiver permissão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PlaceItem criado com sucesso."),
            @ApiResponse(responseCode = "204", description = "Bacia ou item não encontrados."),
            @ApiResponse(responseCode = "409", description = "PlaceItem já existe."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha ao verificar permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a criação.")
    })
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createPlaceItem(
            @RequestBody PlaceItemRequestDTO placeItemRequestDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeItemService.createPlaceItem(placeItemRequestDTO, token);
    }

    @Operation(
            summary = "Exclusão de PlaceItem",
            description = "Exclui um PlaceItem existente com base nos dados fornecidos, se o usuário tiver permissão."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlaceItem deletado com sucesso."),
            @ApiResponse(responseCode = "204", description = "PlaceItem não encontrado."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha ao verificar permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a exclusão.")
    })
    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deletePlaceItem(
            @RequestBody PlaceItemRequestDTO placeItemRequestDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeItemService.deletePlaceItem(placeItemRequestDTO, token);
    }

    @Operation(
            summary = "Verificação de Existência de PlaceItem",
            description = "Verifica se um PlaceItem existe no sistema com base nos dados fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PlaceItem encontrado."),
            @ApiResponse(responseCode = "204", description = "PlaceItem não encontrado."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha ao verificar permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao processar a verificação.")
    })
    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existPlaceItem(
            @RequestBody PlaceItemRequestDTO placeItemRequestDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeItemService.existsByNameAndContract(placeItemRequestDTO, token);
    }

    @Operation(
            summary = "Buscar todos os PlaceItens por Token",
            description = "Obtém todos os PlaceItens associados ao token do usuário autenticado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de PlaceItens retornada com sucesso."),
            @ApiResponse(responseCode = "204", description = "Nenhum PlaceItem encontrado."),
            @ApiResponse(responseCode = "424", description = "Erro em dependências (ex.: falha ao verificar permissões)."),
            @ApiResponse(responseCode = "500", description = "Erro interno ao buscar os PlaceItens.")
    })
    @GetMapping("/get-places-itens-by-token")
    public Mono<ResponseEntity<?>> getAllPlacesItensByToken(
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return placeItemService.getAllPlaceItensByToken(token);
    }
}
