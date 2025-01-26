package controller;

import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import service.ProjectService;

@RestController
@RequestMapping("api/projects")
@Tag(name = "Project API", description = "Endpoints para gerenciamento de obras.")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(
            summary = "Criar uma nova obra",
            description = "Cria uma nova obra com base nos dados fornecidos, se o usuário tiver permissão.",
            requestBody = @RequestBody(
                    description = "Dados para criar uma nova obra.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProjectDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Obra criada com sucesso."),
                    @ApiResponse(responseCode = "403", description = "Permissão negada para criar a obra."),
                    @ApiResponse(responseCode = "404", description = "Ação não encontrada."),
                    @ApiResponse(responseCode = "409", description = "Obra já existe para o contrato fornecido."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao criar a obra.")
            }
    )
    @PostMapping("/create")
    public Mono<ResponseEntity<String>> createProject(
            @RequestBody ProjectDTO projectDTO,
            @Parameter(description = "Token JWT para autenticação do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return projectService.createProject(projectDTO, token);
    }

    @Operation(
            summary = "Deletar uma obra",
            description = "Deleta uma obra existente com base nos dados fornecidos, se o usuário tiver permissão.",
            requestBody = @RequestBody(
                    description = "Dados da obra a ser deletada.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProjectDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Obra deletada com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Obra não encontrada."),
                    @ApiResponse(responseCode = "403", description = "Permissão negada para deletar a obra."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao deletar a obra.")
            }
    )
    @PostMapping("/delete")
    public Mono<ResponseEntity<String>> deleteProject(
            @RequestBody ProjectDTO projectDTO,
            @Parameter(description = "Token JWT para autenticação do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return projectService.deleteProject(projectDTO, token);
    }

    @Operation(
            summary = "Verificar existência de obra",
            description = "Verifica se uma obra existe com base no contrato fornecido, se o usuário tiver permissão.",
            requestBody = @RequestBody(
                    description = "Dados para verificar a existência da obra.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProjectRequestDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Obra encontrada."),
                    @ApiResponse(responseCode = "404", description = "Obra não encontrada."),
                    @ApiResponse(responseCode = "403", description = "Permissão negada para verificar a obra."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao verificar a existência da obra.")
            }
    )
    @PostMapping("/exist")
    public Mono<ResponseEntity<String>> existProject(
            @RequestBody ProjectRequestDTO projectRequestDTO,
            @Parameter(description = "Token JWT para autenticação do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return projectService.existsByContract(projectRequestDTO, token);
    }

    @Operation(
            summary = "Buscar contratos por e-mail de usuário",
            description = "Busca todos os contratos associados a um e-mail de usuário.",
            parameters = @Parameter(
                    name = "userEmail",
                    description = "E-mail do usuário cujos contratos serão buscados.",
                    required = true,
                    example = "user@example.com"
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de contratos encontrados."),
                    @ApiResponse(responseCode = "404", description = "Nenhum contrato encontrado para o e-mail fornecido."),
                    @ApiResponse(responseCode = "403", description = "Permissão negada para buscar contratos."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao buscar contratos.")
            }
    )
    @GetMapping("/get-contracts-by-email")
    public Mono<ResponseEntity<?>> getContractsByEmail(
            @RequestParam("userEmail") String userEmail,
            @Parameter(description = "Token JWT para autenticação do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return projectService.getContractsByUserEmail(userEmail, token);
    }

    @Operation(
            summary = "Adicionar e-mail a uma obra",
            description = "Adiciona um e-mail de usuário a uma obra existente.",
            requestBody = @RequestBody(
                    description = "Dados para adicionar e-mail a uma obra.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ProjectRequestDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "E-mail adicionado com sucesso à obra."),
                    @ApiResponse(responseCode = "400", description = "E-mail já está associado à obra."),
                    @ApiResponse(responseCode = "404", description = "Obra não encontrada."),
                    @ApiResponse(responseCode = "403", description = "Permissão negada para adicionar o e-mail."),
                    @ApiResponse(responseCode = "424", description = "Falha em uma dependência."),
                    @ApiResponse(responseCode = "500", description = "Erro interno ao adicionar e-mail à obra.")
            }
    )
    @PostMapping("/add-email-in-project")
    public Mono<ResponseEntity<String>> addEmailInProject(
            @RequestBody ProjectRequestDTO projectRequestDTO,
            @Parameter(description = "Token JWT para autenticação do usuário.", required = true, example = "Bearer <token>")
            @RequestHeader("Authorization") String token) {
        return projectService.addEmailInProject(projectRequestDTO, token);
    }
}
