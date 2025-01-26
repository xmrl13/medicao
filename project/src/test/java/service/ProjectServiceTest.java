package service;

import client.ProjectClient;
import dto.ProjectDTO;
import dto.ProjectRequestDTO;
import model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import repository.ProjectRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectClient projectClient;

    @InjectMocks
    private ProjectService projectService;

    private ProjectDTO projectDTO;
    private ProjectRequestDTO projectRequestDTO;
    private String token;

    @BeforeEach
    void setup() {
        token = "token-teste";
        projectDTO = new ProjectDTO("Obra 1", "CONTRATO-123", 1000.0);
        projectRequestDTO = new ProjectRequestDTO("CONTRATO-123", "user@example.com");
    }

    @Test
    void createProject_PermissaoNegada() {
        when(projectClient.hasPermission(token, "createProject"))
                .thenReturn(Mono.just(ResponseEntity.status(FORBIDDEN).body("Sem permissão")));

        Mono<ResponseEntity<String>> result = projectService.createProject(projectDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(FORBIDDEN, response.getStatusCode());
                    assertEquals("Sem permissão", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void createProject_ProjetoJaExiste() {
        when(projectClient.hasPermission(token, "createProject"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByNameAndContract(projectDTO.getName(), projectDTO.getContract()))
                .thenReturn(Mono.just(new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget())));


        Mono<ResponseEntity<String>> result = projectService.createProject(projectDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(CONFLICT, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void createProject_Sucesso() {
        when(projectClient.hasPermission(token, "createProject"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByNameAndContract(projectDTO.getName(), projectDTO.getContract()))
                .thenReturn(Mono.empty());

        when(projectRepository.save(any(Project.class)))
                .thenReturn(Mono.just(new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget())));

        Mono<ResponseEntity<String>> result = projectService.createProject(projectDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(CREATED, response.getStatusCode());
                    assertEquals("Obra criada com sucesso", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteProject_ProjetoNaoEncontrado() {
        when(projectClient.hasPermission(token, "deletePlace"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByContract(projectDTO.getContract()))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = projectService.deleteProject(projectDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Obra não encontrada com base no contrato fornecido", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void deleteProject_Sucesso() {
        when(projectClient.hasPermission(token, "deletePlace"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByContract(projectDTO.getContract()))
                .thenReturn(Mono.just(new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget())));

        when(projectRepository.delete(any(Project.class)))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = projectService.deleteProject(projectDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Obra deletada com sucesso", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void existsByContract_ProjetoNaoEncontrado() {
        when(projectClient.hasPermission(token, "existProject"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByContract(projectRequestDTO.getContract()))
                .thenReturn(Mono.empty());

        Mono<ResponseEntity<String>> result = projectService.existsByContract(projectRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(NOT_FOUND, response.getStatusCode());
                    assertEquals("Obra não encontrada", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void existsByContract_ProjetoEncontrado() {
        when(projectClient.hasPermission(token, "existProject"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByContract(projectRequestDTO.getContract()))
                .thenReturn(Mono.just(new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget())));

        Mono<ResponseEntity<String>> result = projectService.existsByContract(projectRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("Obra encontrada", response.getBody());
                })
                .verifyComplete();
    }

    @Test
    void addEmailInProject_EmailJaExiste() {
        when(projectClient.hasPermission(token, "addEmailInProject"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        Project existingProject = new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget());
        existingProject.setUserEmail(List.of("user@example.com"));

        when(projectRepository.findByContract(projectRequestDTO.getContract()))
                .thenReturn(Mono.just(existingProject));

        Mono<ResponseEntity<String>> result = projectService.addEmailInProject(projectRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(BAD_REQUEST, response.getStatusCode());
                })
                .verifyComplete();
    }

    @Test
    void addEmailInProject_EmailAdicionado() {
        when(projectClient.hasPermission(token, "addEmailInProject"))
                .thenReturn(Mono.just(ResponseEntity.ok("Permitido")));

        when(projectRepository.findByContract(projectRequestDTO.getContract()))
                .thenReturn(Mono.just(new Project(projectDTO.getName(), projectDTO.getContract(), projectDTO.getBudget())));

        when(projectRepository.addEmailToProject(projectRequestDTO.getUserEmail(), projectRequestDTO.getContract()))
                .thenReturn(Mono.just(1));

        Mono<ResponseEntity<String>> result = projectService.addEmailInProject(projectRequestDTO, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertEquals(OK, response.getStatusCode());
                    assertEquals("E-mail adicionado com sucesso ao projeto.", response.getBody());
                })
                .verifyComplete();
    }
}
