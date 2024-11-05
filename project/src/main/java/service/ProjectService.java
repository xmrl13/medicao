package service;

import client.ProjectClient;
import dto.ProjectDTO;
import model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import repository.ProjectRepository;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.CREATED;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    private ProjectClient projectClient;

    public Mono<ResponseEntity<String>> createProject(ProjectDTO projectDTO, String token) {
        String action = "createProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {

                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return projectRepository.findByNameAndContract(projectDTO.getName(), projectDTO.getContract())
                            .flatMap(existingProject ->
                                    Mono.just(ResponseEntity.status(CONFLICT)
                                            .body("A obra: " + projectDTO.getName() + " já existe para o contrato: " + projectDTO.getContract()))
                            )
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        Project project = new Project();
                                        project.setName(projectDTO.getName());
                                        project.setContract(projectDTO.getContract());
                                        project.setBudget(projectDTO.getBudget());
                                        project.setUserId(projectDTO.getUserId());
                                        return projectRepository.save(project)
                                                .map(savedProject -> ResponseEntity.status(CREATED)
                                                        .body("Obra criada com sucesso"));
                                    })
                            );
                });
    }


    public Mono<ResponseEntity<String>> deleteProject(ProjectDTO projectDTO, String token) {
        String action = "deletePlace";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return projectRepository.findByNameAndContract(projectDTO.getName(), projectDTO.getContract())
                            .flatMap(existingProject ->
                                    projectRepository.delete((Project) existingProject)
                                            .then(Mono.just(ResponseEntity.status(OK)
                                                    .body("Bacia deletada com sucesso"))))
                            .switchIfEmpty(Mono.just(ResponseEntity.status(NOT_FOUND)
                                    .body("Bacia não encontrada com o nome e contrato de projeto fornecidos")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a exclusão da bacia: " + error.getMessage())));
    }

    public Mono<ResponseEntity<String>> existsByNameAndContract(ProjectDTO projectDTO, String token) {
        String action = "existProject";

        return projectClient.hasPermission(token, action)
                .flatMap(responseEntity -> {
                    HttpStatus status = (HttpStatus) responseEntity.getStatusCode();
                    String message = responseEntity.getBody();

                    if (status == NOT_FOUND) {
                        return Mono.just(ResponseEntity.status(NOT_FOUND)
                                .body("Ação não encontrada: " + action));
                    } else if (status == FORBIDDEN) {
                        return Mono.just(ResponseEntity.status(FORBIDDEN)
                                .body("Sem permissão para realizar essa ação"));
                    } else if (status != OK) {
                        return Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                                .body("Erro ao verificar permissão: " + message));
                    }

                    return projectRepository.findByNameAndContract(projectDTO.getName(), projectDTO.getContract())
                            .flatMap(existingPlace ->
                                    Mono.just(ResponseEntity.status(OK)
                                            .body("Bacia encontrada")))
                            .switchIfEmpty(
                                    Mono.just(ResponseEntity.status(NOT_FOUND)
                                            .body("Bacia não encontrada")));
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(INTERNAL_SERVER_ERROR)
                        .body("Erro ao verificar a existência da bacia: " + error.getMessage())));
    }
}
