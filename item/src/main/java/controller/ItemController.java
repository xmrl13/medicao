package controller;

import dto.ItemDTO;
import dto.ItemRequestDTO;
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
import service.ItemService;

@RestController
@RequestMapping("api/itens")
@Tag(name = "Item Controller", description = "Controlador responsável pela criação, verificação e exclusão de itens.")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(
            summary = "Cria um item",
            description = "Cria um único item com base nas informações fornecidas no corpo da requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item criado com sucesso."),
            @ApiResponse(responseCode = "409", description = "O item já existe."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (token, role, etc.)."),
            @ApiResponse(responseCode = "403", description = "Ação não autorizada. Verifique o token."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada."),
            @ApiResponse(responseCode = "424", description = "Falha em um serviço dependente."),
            @ApiResponse(responseCode = "500", description = "Erro interno no serviço de itens.")
    })
    @PostMapping("/create/one")
    public Mono<ResponseEntity<String>> createOne(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informações do item a ser criado.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ItemDTO.class),
                            mediaType = "application/json"
                    )
            )
            @RequestBody ItemDTO itemDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return itemService.createItem(itemDTO, token);
    }

    @Operation(
            summary = "Deleta um item",
            description = "Exclui um item com base nas informações fornecidas no corpo da requisição."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item deletado com sucesso."),
            @ApiResponse(responseCode = "204", description = "Item não encontrado."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (token, role, etc.)."),
            @ApiResponse(responseCode = "403", description = "Ação não autorizada. Verifique o token."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada."),
            @ApiResponse(responseCode = "424", description = "Falha em um serviço dependente."),
            @ApiResponse(responseCode = "500", description = "Erro interno no serviço de itens.")
    })
    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informações do item a ser deletado.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ItemDTO.class),
                            mediaType = "application/json"
                    )
            )
            @RequestBody ItemDTO itemDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return itemService.deleteItem(itemDTO, token);
    }

    @Operation(
            summary = "Verifica se um item existe",
            description = "Verifica se um item existe no sistema com base no nome e na unidade fornecidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item encontrado."),
            @ApiResponse(responseCode = "204", description = "Item não encontrado."),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (token, role, etc.)."),
            @ApiResponse(responseCode = "403", description = "Ação não autorizada. Verifique o token."),
            @ApiResponse(responseCode = "404", description = "Ação não encontrada."),
            @ApiResponse(responseCode = "424", description = "Falha em um serviço dependente."),
            @ApiResponse(responseCode = "500", description = "Erro interno no serviço de itens.")
    })
    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para verificar a existência do item.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ItemRequestDTO.class),
                            mediaType = "application/json"
                    )
            )
            @RequestBody ItemRequestDTO itemRequestDTO,
            @Parameter(description = "Token de autorização do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return itemService.existsByNameAndUnit(itemRequestDTO, token);
    }
}
