package controller;

import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import service.ProjectService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProjectControllerTest {

    private ProjectService projectService;
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        projectService = Mockito.mock(ProjectService.class);
        projectController = new ProjectController(projectService);
    }

    @Test
    @DisplayName("Deve criar um projeto com sucesso")
    void createProject_Success() {
        ProjectDTO projectDTO = new ProjectDTO("SES001", "CONTRATO-123", 1000.0);
        String token = "Bearer valid-token";

        when(projectService.createProject(eq(projectDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Projeto criado com sucesso")));

        Mono<ResponseEntity<String>> response = projectController.createProject(projectDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.CREATED, result.getStatusCode());
            assertEquals("Projeto criado com sucesso", result.getBody());
        });

        verify(projectService, times(1)).createProject(eq(projectDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao criar projeto sem permissão")
    void createProject_Forbidden() {
        ProjectDTO projectDTO = new ProjectDTO("SES001", "CONTRATO-123", 1000.0);
        String token = "Bearer invalid-token";

        when(projectService.createProject(eq(projectDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão para criar o projeto")));

        Mono<ResponseEntity<String>> response = projectController.createProject(projectDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
            assertEquals("Sem permissão para criar o projeto", result.getBody());
        });

        verify(projectService, times(1)).createProject(eq(projectDTO), eq(token));
    }

    @Test
    @DisplayName("Deve deletar um projeto com sucesso")
    void deleteProject_Success() {
        ProjectDTO projectDTO = new ProjectDTO("SES001", "CONTRATO-123", 1000.0);
        String token = "Bearer valid-token";

        when(projectService.deleteProject(eq(projectDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("Projeto deletado com sucesso")));

        Mono<ResponseEntity<String>> response = projectController.deleteProject(projectDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Projeto deletado com sucesso", result.getBody());
        });

        verify(projectService, times(1)).deleteProject(eq(projectDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao deletar um projeto inexistente")
    void deleteProject_NotFound() {
        ProjectDTO projectDTO = new ProjectDTO("SES001", "CONTRATO-123", 1000.0);
        String token = "Bearer valid-token";

        when(projectService.deleteProject(eq(projectDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado")));

        Mono<ResponseEntity<String>> response = projectController.deleteProject(projectDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("Projeto não encontrado", result.getBody());
        });

        verify(projectService, times(1)).deleteProject(eq(projectDTO), eq(token));
    }

    @Test
    @DisplayName("Deve verificar existência de um projeto com sucesso")
    void existProject_Success() {
        ProjectRequestDTO projectRequestDTO = new ProjectRequestDTO("CONTRATO-123", null);
        String token = "Bearer valid-token";

        when(projectService.existsByContract(eq(projectRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("Projeto encontrado")));

        Mono<ResponseEntity<String>> response = projectController.existProject(projectRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Projeto encontrado", result.getBody());
        });

        verify(projectService, times(1)).existsByContract(eq(projectRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve retornar erro ao verificar existência de projeto inexistente")
    void existProject_NotFound() {
        ProjectRequestDTO projectRequestDTO = new ProjectRequestDTO("CONTRATO-123", null);
        String token = "Bearer valid-token";

        when(projectService.existsByContract(eq(projectRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado")));

        Mono<ResponseEntity<String>> response = projectController.existProject(projectRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
            assertEquals("Projeto não encontrado", result.getBody());
        });

        verify(projectService, times(1)).existsByContract(eq(projectRequestDTO), eq(token));
    }

    @Test
    @DisplayName("Deve buscar contratos por e-mail com sucesso")
    void getContractsByEmail_Success() {
        String userEmail = "user@example.com";
        String token = "Bearer valid-token";

        when(projectService.getContractsByUserEmail(eq(userEmail), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.ok("Lista de contratos retornada")));

        Mono<ResponseEntity<?>> response = projectController.getContractsByEmail(userEmail, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("Lista de contratos retornada", result.getBody());
        });

        verify(projectService, times(1)).getContractsByUserEmail(eq(userEmail), eq(token));
    }

    @Test
    @DisplayName("Deve adicionar e-mail a um projeto com sucesso")
    void addEmailInProject_Success() {
        ProjectRequestDTO projectRequestDTO = new ProjectRequestDTO("CONTRATO-123", "user@example.com");
        String token = "Bearer valid-token";

        when(projectService.addEmailInProject(eq(projectRequestDTO), eq(token)))
                .thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).body("E-mail adicionado com sucesso ao projeto")));

        Mono<ResponseEntity<String>> response = projectController.addEmailInProject(projectRequestDTO, token);

        response.subscribe(result -> {
            assertEquals(HttpStatus.OK, result.getStatusCode());
            assertEquals("E-mail adicionado com sucesso ao projeto", result.getBody());
        });

        verify(projectService, times(1)).addEmailInProject(eq(projectRequestDTO), eq(token));
    }
}
